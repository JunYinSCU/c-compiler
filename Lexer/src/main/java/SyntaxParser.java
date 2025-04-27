import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxParser {
    private LinkedList<Token> tokens;   //token列表
    private Token EOFToken = new Token("EOF", "$", -1, -1);     // 文件结束符
    private int current = 0;    // 当前token指针
    private String outputFile = "SyntaxOutput.txt";     // 输出文件名
    private BufferedWriter output;
    private int errorNum = 0; // 语法错误数量

    public LinkedList<Token> getTokens() {
        return this.tokens;
    }


    public SyntaxParser(LinkedList<Token> tokens, String outputFile) {
        LinkedList<Token> filteredTokens = new LinkedList<>();
        for (Token t : tokens) {    // 过滤掉注释token
            if (!t.getType().equals("COMMENT")) {
                filteredTokens.add(t);
            }
        }
        this.tokens = filteredTokens;
        this.tokens.addLast(EOFToken);  //添加文件结束符
        this.outputFile = outputFile;
        try {
            output = new BufferedWriter(new FileWriter(this.outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SyntaxParser(LinkedList<Token> tokens) {
        LinkedList<Token> filteredTokens = new LinkedList<>();
        for (Token t : tokens) {    // 过滤掉注释token
            if (!t.getType().equals("COMMENT")) {
                filteredTokens.add(t);
            }
        }
        this.tokens = filteredTokens;
        this.tokens.addLast(EOFToken); //添加文件结束符
        try {
            output = new BufferedWriter(new FileWriter(this.outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 将current指针向前移动一位
     * 
     */
    private void advance() {
        if(current < tokens.size() - 1){
            current++;
        }
    }

    /*
     * 向后移动一位
     */
    private void back() {
        current--;
    }

    /*
     * 判断是否到达tokens末尾
     */
    private boolean isAtEnd() {
        return getCurrent().getType() == EOFToken.getType();
    }

    /*
     * 获得当前token，指针不移动
     */
    private Token getCurrent() {
        if(current < tokens.size()){
            return tokens.get(current);
        }
        return null;
    }

    /*
     * 获取前一个token，指针不移动
     */
    private Token previous() {
        return tokens.get(current - 1);
    }
    /*
     * 获取下一个token，指针不移动
     */
    private Token next() {
        return tokens.get(current + 1);
    }

    /*
     *
     * match方法用于判断当前token的类型是否与传入的token的类型相同
     * 先判断传入token的值是否为null，是则说明只需比较类型
     * 否则比较类型和值
     * 返回值：true/false
     */
    private boolean match(Token expectedToken) {
        if (isAtEnd())
            return false;

        if(expectedToken.getValue().equals("null")){
            if(getCurrent().getType().equals(expectedToken.getType())){
                return true;
            }
        }else{
            if(getCurrent().getType().equals(expectedToken.getType()) && getCurrent().getValue().equals(expectedToken.getValue())){
                return true;
            }
        }
        return false;
    }

    /*
     * 判断实际token和预期token是否相等，判断方法同match方法
     */
    private boolean isEqual(Token actualToken, Token expectedToken) {
        if(expectedToken.getValue().equals("null")){
            return actualToken.getType().equals(expectedToken.getType());
        }
        return actualToken.getType().equals(expectedToken.getType()) && actualToken.getValue().equals(expectedToken.getValue());
    }

    /*
     * 获得前面第n个token
     * 返回值：Token对象
     */
    private Token lookAheadN(int n) {
        if (current + n >= tokens.size()) {
            return EOFToken;
        }
        return tokens.get(current + n);
    }

    /*
     * consume方法只用于匹配终结符token，调用match方法进行匹配
     * 无论是否匹配成功，都会将指针向前移动一位
     * 如果匹配失败，则调用error方法,提示当前token错误
     */
    private void consume(Token expectedToken) {
        if (!match(expectedToken)) {
            error(getCurrent());     
        }
        advance();     
    }

    private void error(Token token) {
        String errorMessage = "Error: " + token.getValue() + " is not a valid token at line " + token.getRow()
                + ", column " + token.getColum();
        System.out.println(errorMessage);
        errorNum++;
    }

    private void error(String expected, Token currentToken) {
        System.out.println(expected);
        String errorMessage = "Current: " + currentToken.getValue() + " is not a valid token at line "
                + currentToken.getRow() + ", column " + currentToken.getColum();
        System.out.println(errorMessage);
        errorNum++;
    }

    /*
     * 通过传入的参数，寻找第一个匹配的位置，继续进行下一步语法判断
     * 如果没有找到，则继续向后移动指针，直到找到为止
     */
    private void synchronize(String... expectedStarts) {
        Token currentToken = getCurrent();
        while (currentToken != null) {
            for (String start : expectedStarts) {
                if (currentToken.getValue().equals(start)) {
                    return;
                }
            }
            if (isStatement() || isTypeSpecifier()) {
                return;
            }
            advance();
        }
    }

    /*
     * 主程序入口
     */
    public void parse() {
        program();      
        System.out.println("Syntax analysis completed.");
        if(errorNum > 0) {
            System.out.println("Total syntax errors: " + errorNum);
        } else {
            System.out.println("No syntax errors found.");
        }
    }

    /*
     * 文法1
     */
    private void program() {
        System.out.println("program");
        declaration_list();
        //语法分析完成
        //如果还有多余的token，则说明语法存在错误
        if(!isAtEnd()) {
            error("Error: Unexpected tokens at the end of input: ",getCurrent());
        }
    }

    /*
     * 文法2.1
     */
    private void declaration_list() {
        System.out.println("declaration_list");
        declaration();
        declaration_list1();
    }

    /*
    * 文法2.2
    * 
    */
    private void declaration_list1() {
        System.out.println("declaration_list1");
        //var_declaration和fun_declaration类型定义实际都归结于type_specifier的int或者void
        while(isTypeSpecifier()) {
            declaration();
        }
        // ε 时不处理
    }

    /*
    * 文法3
    * 
    */
    private void declaration() {
        System.out.println("declaration");
        //todo：完善var_declaration和fun_declaration调用的判定条件
    
        if(isTypeSpecifier()){
            Token nextNext = lookAheadN(2); //获取第二个token判断是var还是fun            
            if(nextNext.getValue().equals(";") ||nextNext.getValue().equals("[")){
                var_declaration();
            }else if(nextNext.getValue().equals( "(")){
                fun_declaration();
            }
        }else{          
            error("Error: Expected 'int' or 'void' at the beginning of a declaration", getCurrent());
            //synchronize("int", "void", "if", "while", "return", "{", ";");
        }
    }

    /*
     * 文法4
     */
    private void var_declaration() {
        System.out.println("var_declaration");
        type_specifier();
        //对终结符进行匹配消耗
        consume(new Token("ID","null"));
        if(match(new Token("SEPARATOR","["))){
            consume(new Token("SEPARATOR","["));
            consume(new Token("NUM","null"));
            consume(new Token("SEPARATOR","]"));
        }
        consume(new Token("SEPARATOR",";"));
    }

    /*
     * 文法5
     */
    private void type_specifier(){
        System.out.println("type_specifier");
        Token INTtoken = new Token("KEYWORD","int");
        Token VOIDtoken = new Token("KEYWORD","void");

        if(match(INTtoken)){
            consume(INTtoken);
        }else if(match(VOIDtoken)){
            consume(VOIDtoken);
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Error: Expected 'int' or 'void'",getCurrent());
            advance();
        }
    }

    private boolean isTypeSpecifier(){
        Token INTtoken = new Token("KEYWORD","int");
        Token VOIDtoken = new Token("KEYWORD","void");
        return match(INTtoken) || match(VOIDtoken);
    }

    /*
     * 文法6
     */
    private void fun_declaration() {
        System.out.println("fun_declaration");
        type_specifier();
        consume(new Token("ID","null"));
        consume(new Token("SEPARATOR","("));
        params();
        consume(new Token("SEPARATOR",")"));
        compound_stmt();
    }

    

    
    /*
     * 文法7
     */
    private void params(){
        System.out.println("params");
        Token voidToken = new Token("KEYWORD","void");
        if(match(voidToken)){
            consume(voidToken);
        }else{
            param_list();
        }
    }

    /*
     * 文法8.1
     */
    private void param_list(){
        System.out.println("param_list");
        param();
        param_list1();
    }

    /*
     * 文法8.2
     */
    private void param_list1(){
        System.out.println("param_list1");
        Token commaToken = new Token("SEPARATOR",",");
        while(match(commaToken)){
            consume(commaToken);
            param();
        }
        // ε 时不处理

    }

    /*
     * 文法9
     */
    private void param(){
        System.out.println("param");
        type_specifier();

        Token ID = new Token("ID","null");       
        consume(ID);

        Token leftBracket = new Token("SEPARATOR","[");
        if(match(leftBracket)){
            consume(leftBracket);
            consume(new Token("SEPARATOR","]"));
        }
    }

    /*
     * 文法10
     */
    private void compound_stmt(){
        System.out.println("compound_stmt");
        consume(new Token("SEPARATOR","{"));
        local_declarations();
        statement_list();
        consume(new Token("SEPARATOR","}"));
    }

    /*
     * 文法11.1
     * 
     */
    private void local_declarations(){
        System.out.println("local_declarations");
        local_declarations1();
    }

    /*
     * 文法11.2
     * 
     */
    private void local_declarations1(){
        System.out.println("local_declarations1");
        while(isTypeSpecifier()){
            var_declaration();
        }
        // ε 时不处理
    }

    /*
     * 文法12.1
     * 
     */
    private void statement_list(){
        System.out.println("statement_list");
        statement_list1();
    }

    /*
     * 文法12.2
     * 
     */
    private void statement_list1(){
        System.out.println("statement_list1");
        while(isStatement()){
            statement();
        }
        // ε 时不处理
    }

    private boolean isStatement() {
        return isExpressionStmt() || isCompoundStmt() || isSelectionStmt() ||
                isIterationStmt() || isReturnStmt();
    }

    /*
     * 文法13
     * 为expression_stmt、compound_stmt、selection_stmt、iteration_stmt、return_stmt时进入statement
     */
    private void statement() {
        System.out.println("statement");
        if(!isAtEnd()){
            if(isExpressionStmt()){
                expression_stmt();
            }else if(isCompoundStmt()){
                compound_stmt();
            }else if(isSelectionStmt()){
                selection_stmt();
            }else if(isIterationStmt()){
                iteration_stmt();
            }else if(isReturnStmt()){
                return_stmt();
            }else{
                //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
                error("Unexpected token at the start of a statement.",getCurrent());
                advance();
            }
        }else{
            error("Expected a statement but found end of input", getCurrent());
        }
        
    }

    private boolean isExpressionStmt() {
        //当为expression或者;时，进入expression_stmt函数
        return isExpression() || match(new Token("SEPARATOR", ";"));
    }

    private boolean isCompoundStmt() {  //块语句以 { 开头
        return match(new Token("SEPARATOR", "{"));
    }

    private boolean isSelectionStmt() { //选择语句以 if 开头    
        return match(new Token("KEYWORD", "if"));
    }

    private boolean isIterationStmt() { //循环语句以 while 开头
        return match(new Token("KEYWORD", "while"));
    }
    private boolean isReturnStmt() {    //返回语句以 return 开头
        return match(new Token("KEYWORD", "return"));
    }

    /*
     * 文法14
     * 为expression或者;时进入expression_stmt
     */
    private void expression_stmt() {
        System.out.println("expression_stmt");
        if(isExpression()){
            expression();
        } 
        consume(new Token("SEPARATOR",";"));
    }

    private boolean isExpression() {
        // 层层归结  最后当token为( ID  NUM 时进入expression
        return match(new Token("ID", "null")) || match(new Token("NUM", "null")) ||
                match(new Token("SEPARATOR","("));
    }


    /*
     * 文法15
     * 
     */
    private void selection_stmt(){
        System.out.println("selection_stmt");
        consume(new Token("KEYWORD","if"));
        consume(new Token("SEPARATOR","("));
        expression();
        consume(new Token("SEPARATOR",")"));
        statement();
        if(match(new Token("KEYWORD","else"))){
            consume(new Token("KEYWORD","else"));
            statement();
        }
    }

    /*
     * 文法16
     * 
     */
    private void iteration_stmt(){
        System.out.println("iteration_stmt");
        consume(new Token("KEYWORD","while"));
        consume(new Token("SEPARATOR","("));
        expression();
        consume(new Token("SEPARATOR",")"));
        statement();
    }

    /*
     * 文法17
     * 
     */
    private void return_stmt(){
        System.out.println("return_stmt");
        consume(new Token("KEYWORD","return"));
        if(!match(new Token("SEPARATOR",";"))){
            expression();
        }
        consume(new Token("SEPARATOR",";"));
    }

    /*
     * 文法18
     * 为var或者simple_expression时进入expression
     * expression文法定义较为复杂
     */
    private void expression(){
        System.out.println("expression");
        // 先保存当前Token位置，方便回溯
        int savePos = current;

        if (isVar()) {
            // 先尝试识别var
            var();

            // 看下一个Token
            Token next = getCurrent();
            if (isEqual(next, new Token("OPERATOR", "="))) {
                // 确认是赋值表达式 var = expression
                consume(new Token("OPERATOR", "="));
                expression();
                return;
            } else {
                // 不是赋值，回溯到var前的位置，识别simple-expression
                current = savePos;
            }
        }

        // 否则走 simple-expression
        simple_expression();  
    }

    private boolean isVar() {
        //var以ID开头
        return match(new Token("ID","null"));
    }

    private boolean isSimpleExpression(Token token) {
        return isEqual(token,new Token("ID","null")) || isEqual(token,new Token("NUM","null")) ||
                isEqual(token,new Token("SEPARATOR","("));
    }

    /*
     * 文法19
     * 为ID时进入var
     */
    private void var() {
        System.out.println("var");
        consume(new Token("ID","null"));
        if(match(new Token("SEPARATOR","["))){
            consume(new Token("SEPARATOR","["));
            expression();
            consume(new Token("SEPARATOR","]"));
        }
    }

    /*
     * 文法20
     * 第一个为term时进入simple_expression
     */
    private void simple_expression() {
        System.out.println("simple_expression");
        additive_expression();
        if (isRelop()) {
            relop();
            additive_expression();
        }
    }

    private boolean isRelop() {
        return match(new Token("OPERATOR", "<")) || match(new Token("OPERATOR", "<=")) ||
               match(new Token("OPERATOR", "==")) || match(new Token("OPERATOR", "!=")) ||
               match(new Token("OPERATOR", ">")) || match(new Token("OPERATOR", ">="));
    }

    /*
     * 文法21
     * 
     */
    private void relop() {
        System.out.println("relop");
        if(isRelop()){
            advance();
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Error: Expected relop e.g. <= < >= > ...",getCurrent());
            advance();
        }
    }

    /*
     * 文法22.1
     * 
     */
    private void additive_expression() {
        System.out.println("additive_expression");
        term();
        additive_expression1();
    }

    /*
     * 文法22.2
     * 
     */
    private void additive_expression1() {
        System.out.println("additive_expression1");
        while(isAddop()){
            addop();
            term();
        }
        
    }

    private boolean isAddop() {
        // + -
        return match(new Token("OPERATOR", "+")) || match(new Token("OPERATOR", "-"));
    }

    /*
     * 文法23
     * 
     */
    private void addop(){
        System.out.println("addop");
        if(isAddop()){
            advance();
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Error: Expected '+' or '-'",getCurrent());
            advance();
        }
    }

    /*
     * 文法24.1
     * 为factor时进入term
     */
    private void term() {
        System.out.println("term");
        factor();
        term1();
    }

    /*
     * 文法24.2
     * 
     */
    private void term1() {
        System.out.println("term1");
        while(isMulop()){
            mulop();
            factor();
        }
    }

    private boolean isMulop() {
        // * /
        return match(new Token("OPERATOR", "*")) || match(new Token("OPERATOR", "/"));
    }

    /* 
     * 文法25
     */
    private void mulop(){
        System.out.println("mulop");
       if(isMulop()){
            advance();
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Error: Expected '*' or '/'",getCurrent());
            advance();
        }
    }

    /*
     * 文法26
     * 为( ID  NUM时进入factor 
     */
    private void factor() {
        System.out.println("factor");
        Token leftBracket = new Token("SEPARATOR", "(");
        Token ID = new Token("ID", "null");
        Token NUM = new Token("NUM", "null");

        if(!isAtEnd()){
            if (match(leftBracket)) {   //匹配 ( , 则为(expression)的情况
                consume(leftBracket);
                expression();
                consume(new Token("SEPARATOR", ")"));
            } else if (match(ID)) {     //匹配ID，可能是函数调用或者变量
                Token leftBracket1 = lookAheadN(1);     //继续获得下一个token
                if(isEqual(leftBracket,leftBracket1)){      //如果下一个token为(，则为函数调用
                    call();
                }else{      //否则为变量
                    var();
                }
            } else if (match(NUM)) {    //匹配NUM，直接消耗
                consume(NUM);
            } else {
                // 如果都不匹配，则说明当前是错误的token,提示错误并向前移动
                error("Error: Expected '(expression)', ID, call func or NUM as a factor",getCurrent());
                //todo: 寻找可能的factor位置
                //synchronize("(", TokenType.ID.toString(), TokenType.NUM.toString());
                advance();
            }
        }else{
            error("Expected a factor but found end of input", getCurrent());
        }
        
    }

    /*
     * 文法27
     * 
     */
    private void call() {
        System.out.println("call");
        consume(new Token("ID","null"));
        consume(new Token("SEPARATOR","("));
        args();
        consume(new Token("SEPARATOR",")"));
    }

    /*
     * 文法28
     * 
     */
    private void args() {
        System.out.println("args");
        if(isExpression()){
            args_list();
        }
             
    }

    /*
     * 文法29.1
     * 
     */
    private void args_list() {
        System.out.println("args_list");
        expression();
        args_list1();
    }

    /*
     * 文法29.2
     * 
     */
    private void args_list1() {
        System.out.println("args_list1");
        Token comma = new Token("SEPARATOR", ",");
        while (match(comma)) {
            consume(comma);
            expression();
        }
    }

}
