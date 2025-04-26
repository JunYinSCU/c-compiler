import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxParser {
    private LinkedList<Token> tokens;
    private Token EOFToken = new Token("EOF", "$", -1, -1);
    private int current = 0;

    private String inputFile = "input.txt";
    private String outputFile = "output.txt";
    private BufferedWriter output;

    private LinkedList<Token> getTokensFormFile(String inputFile) {
        LinkedList<Token> tokens = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 使用正则表达式匹配 Token 字符串的各个部分
                Pattern pattern = Pattern.compile("<\\s*([^,\\s]+)\\s*,\\s*([^,\\s]+)\\s*,\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)\\s*>");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String type = matcher.group(1).trim();
                    String value = matcher.group(2).trim();
                    int row = Integer.parseInt(matcher.group(3).trim());
                    int column = Integer.parseInt(matcher.group(4).trim());
                    tokens.add(new Token(type, value, row, column));
                }
            }
        } catch (IOException e) {
            System.err.println("读取文件时发生错误: " + e.getMessage());
        }
        return tokens;
    }

    public SyntaxParser(String inputFile, String outputFile) {
        
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.tokens = getTokensFormFile(inputFile);
        tokens.addLast(EOFToken);
        try {
            output = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public SyntaxParser(LinkedList<Token> tokens, String outputFile) {
        this.tokens = tokens;
        try {
            output = new BufferedWriter(new FileWriter(this.outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public SyntaxParser() {
        this.tokens = getTokensFormFile(inputFile);
        tokens.addLast(EOFToken);
        try {
            output = new BufferedWriter(new FileWriter(this.outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 将current指针向前移动一位，返回前一个token
     * 返回值：移动后的前一个token
     */
    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    /*
     * 判断是否到达tokens末尾
     */
    private boolean isAtEnd() {
        return peek().getType() == EOFToken.getType();
    }

    /*
     * 获得当前token，指针不移动
     */
    private Token peek() {
        return tokens.get(current);
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
    private boolean match(Token t) {
        if (isAtEnd())
            return false;

        if(t.getValue().equals("null")){
            if(peek().getType().equals(t.getType())){
                return true;
            }
        }else{
            if(peek().getType().equals(t.getType()) && peek().getValue().equals(t.getValue())){
                return true;
            }
        }
        return false;
    }

    boolean isEqual(Token t1, Token t2) {
        if(t2.getValue().equals("null")){
            return t1.getType().equals(t2.getType());
        }
        return t1.getType().equals(t2.getType()) && t1.getValue().equals(t2.getValue());
    }

    /*
     * 查看后面第n个token
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
    private void consume(Token token) {
        if (!match(token)) {
            error(peek());     
        }
        advance();     
    }

    private void error(Token token) {
        String errorMessage = "Error: " + token.getValue() + " is not a valid token at line " + token.getRow()
                + ", column " + token.getColum();
        System.out.println(errorMessage);
    }

    /*
     * 主程序入口
     */
    public void parse() {
        program();
    }
    /*
     * 文法1
     */
    private void program() {
        declaration_list();
    }

    /*
     * 文法2.1
     */
    private void declaration_list() {
        declaration();
        declaration_list1();
    }

    /*
    * 文法2.2
     * 
    */
    private void declaration_list1() {
        //var_declaration和fun_declaration类型定义实际都归结于type_specifier的int或者void
        Token INTtoken = new Token("KEYWORD","int");
        Token VOIDtoken = new Token("KEYWORD","void");
        while(match(INTtoken) || match(VOIDtoken)) {
            declaration();
        }
    }

    /*
    * 文法3
    * 
    */
    private void declaration() {
        //todo：完善var_declaration和fun_declaration调用的判定条件
        Token judgeToken = lookAheadN(3);
        if(judgeToken.getValue().equals(";") ||judgeToken.getValue().equals("[")){
            var_declaration();
        }else if(judgeToken.getValue() == "("){
            fun_declaration();
        }else {
            fun_declaration();
        }
    }

    /*
     * 文法4
     */
    private void var_declaration() {
        type_specifier();

        consume(new Token("ID","null"));
        if(match(new Token("SEPARATOR","["))){
            consume(new Token("SEPARATOR","["));
            consume(new Token("NUM","null"));
            consume(new Token("SEPARATOR","]"));
        }
        consume(new Token("SEPARATOR",";"));
    }
    /*
     * 文法6
     */
    private void fun_declaration() {
        type_specifier();
        consume(new Token("ID","null"));
        consume(new Token("SEPARATOR","("));
        params();
        consume(new Token("SEPARATOR",")"));
        compound_stmt();
    }
    /*
     * 文法5
     */
    private void type_specifier(){
        Token INTtoken = new Token("KEYWORD","int");
        Token VOIDtoken = new Token("KEYWORD","void");

        if(match(INTtoken)){
            consume(INTtoken);
        }else if(match(VOIDtoken)){
            consume(VOIDtoken);
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error(peek());
            advance();
        }
    }

    /*
     * 文法7
     */
    private void params(){
        Token token = new Token("KEYWORD","void");
        if(match(token)){
            consume(token);
        }else{
            param_list();
        }
    }

    /*
     * 文法10
     */
    private void compound_stmt(){
        consume(new Token("SEPARATOR","{"));
        local_declarations();
        statement_list();
        consume(new Token("SEPARATOR","}"));
    }

    /*
     * 文法8.1
     */
    private void param_list(){
        param();
        param_list1();
    }

    /*
     * 文法8.2
     */
    private void param_list1(){
        Token token = new Token("SEPARATOR",",");
        while(match(token)){
            consume(token);
            param();
        }

    }

    /*
     * 文法9
     */
    private void param(){
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
     * 文法11.1
     * 
     */
    private void local_declarations(){
        local_declarations1();
    }
    /*
     * 文法11.2
     * 
     */
    private void local_declarations1(){
        Token INTtoken = new Token("KEYWORD","int");
        Token VOIDtoken = new Token("KEYWORD","void");
        while(match(INTtoken) || match(VOIDtoken)){
            var_declaration();
        }
        //todo:如果为空时
    }

    /*
     * 文法12.1
     * 
     */
    private void statement_list(){
        statement_list1();
    }

    /*
     * 文法12.2
     * 
     */
    private void statement_list1(){
        
        while(isStatement()){
            statement();
        }
        //todo:如果为空时
    }

    boolean isStatement() {
        return isExpressionStmt() || isCompoundStmt() || isSelectionStmt() ||
                isIterationStmt() || isReturnStmt();
    }

    /*
     * 文法13
     * 为expression_stmt、compound_stmt、selection_stmt、iteration_stmt、return_stmt时进入statement
     */
    private void statement() {
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
            error(peek());
            advance();
        }
    }

    boolean isExpressionStmt() {
        return isExpression() || match(new Token("SEPARATOR", ";"));
    }


    boolean isCompoundStmt() {
        return match(new Token("SEPARATOR", "{"));
    }

    boolean isSelectionStmt() {
        return match(new Token("KEYWORD", "if"));
    }

    boolean isIterationStmt() {
        return match(new Token("KEYWORD", "while"));
    }
    boolean isReturnStmt() {
        return match(new Token("KEYWORD", "return"));
    }

    /*
     * 文法14
     * 为expression或者;时进入expression_stmt
     */
    private void expression_stmt() {
        if(isExpression()){
            expression();
        }  
        consume(new Token("SEPARATOR",";"));
    }

    // ( ID  NUM 时进入expression
    boolean isExpression() {
        return match(new Token("ID", "null")) || match(new Token("NUM", "null")) ||
                match(new Token("SEPARATOR","("));
    }
    /*
     * 文法15
     * 
     */
    private void selection_stmt(){
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
        consume(new Token("KEYWORD","return"));
        if(!match(new Token("SEPARATOR",";"))){
            expression();
        }
        consume(new Token("SEPARATOR",";"));
    }

    /*
     * 文法18
     * 为var或者simple_expression时进入expression
     */
    private void expression(){
        //todo:完善判断逻辑
        Token Assign = new Token("OPERATOR","=");
        if(isVar()){
            var();
            if(match(Assign)){
                consume(Assign);
                expression();
            }else{
                simple_expression();
            }
        }else{
            simple_expression();
        }    
    }

    boolean isVar() {
        return match(new Token("ID","null"));
    }

    /*
     * 文法19
     * 为ID时进入var
     */
    private void var() {
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
        additive_expression();
        if (isRelop()) {
            relop();
            additive_expression();
        }
    }

    boolean isRelop() {
        return match(new Token("OPERATOR", "<")) || match(new Token("OPERATOR", "<=")) ||
               match(new Token("OPERATOR", "==")) || match(new Token("OPERATOR", "!=")) ||
               match(new Token("OPERATOR", ">")) || match(new Token("OPERATOR", ">="));
    }

    /*
     * 文法21
     * 
     */
    private void relop() {
        Token less = new Token("OPERATOR", "<");
        Token lessEqual = new Token("OPERATOR", "<=");
        Token equal = new Token("OPERATOR", "==");
        Token notEqual = new Token("OPERATOR", "!=");
        Token greater = new Token("OPERATOR", ">");
        Token greaterEqual = new Token("OPERATOR", ">=");
        if(match(less)){
            consume(less);         
        }else if(match(lessEqual)){
            consume(lessEqual);
        }else if(match(equal)){
            consume(equal);
        }else if(match(notEqual)){
            consume(notEqual);
        }else if(match(greater)){
            consume(greater);
        }else if(match(greaterEqual)){
            consume(greaterEqual);
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error(peek());
            advance();
        }
    }

    /*
     * 文法22.1
     * 
     */
    private void additive_expression() {
        term();
        additive_expression1();
    }

    /*
     * 文法22.2
     * 
     */
    private void additive_expression1() {
        while(isAddop()){
            addop();
            term();
            additive_expression1();
        }
        //todo:如果为空时

        //todo：另一种想法，只不过需要合并additive_expression1和addop的处理，需要判断哪种更合理
        // Token plus = new Token("OPERATOR", "+");
        // Token minus = new Token("OPERATOR", "-");
        // if(match(plus)){
        //     consume(plus);
        //     term();
        //     additive_expression1();
        // }else if(match(minus)){
        //     consume(minus);
        //     term();
        //     additive_expression1();
        // }else{
        //     //todo：如果为空
        // }
        
    }

    boolean isAddop() {
        return match(new Token("OPERATOR", "+")) || match(new Token("OPERATOR", "-"));
    }

    /*
     * 文法23
     * 
     */
    private void addop(){
        Token plus = new Token("OPERATOR", "+");
        Token minus = new Token("OPERATOR", "-");
        if(match(plus)){
            consume(plus);
        }else if(match(minus)){
            consume(minus);
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error(peek());
            advance();
        }
    }

    /*
     * 文法24.1
     * 为factor时进入term
     */
    private void term() {
        factor();
        term1();
    }

    /*
     * 文法24.2
     * 
     */
    private void term1() {
        while(isMulop()){
            mulop();
            factor();
            term1();
        }
    }

    boolean isMulop() {
        return match(new Token("OPERATOR", "*")) || match(new Token("OPERATOR", "/"));
    }

    /* 
     * 文法25
     */
    private void mulop(){
        Token mul = new Token("OPERATOR", "*");
        Token div = new Token("OPERATOR", "/");
        if(match(mul)){
            consume(mul);
        }else if(match(div)){
            consume(div);
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error(peek());
            advance();
        }
    }

    /*
     * 文法26
     * 为( ID  NUM时进入factor 
     */
    private void factor() {
        Token leftBracket = new Token("SEPARATOR", "(");
        Token ID = new Token("ID", "null");
        Token NUM = new Token("NUM", "null");
        if (match(leftBracket)) {
            consume(leftBracket);
            expression();
            consume(new Token("SEPARATOR", ")"));
        } else if (match(ID)) {
            //consume(ID);
            Token leftBracket1 = lookAheadN(1);
            if(isEqual(leftBracket,leftBracket1)){
                call();
            }else{
                var();
            }
        } else if (match(NUM)) {
            consume(NUM);
        } else {
            // 如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error(peek());
            advance();
        }
    }

    /*
     * 文法27
     * 
     */
    private void call() {
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
        if(isExpression()){
            args_list();
        }
        //todo:如果为空时       
    }

    /*
     * 文法29.1
     * 
     */
    private void args_list() {
        expression();
        args_list1();
    }

    /*
     * 文法29.2
     * 
     */
    private void args_list1() {
        Token comma = new Token("SEPARATOR", ",");
        while (match(comma)) {
            consume(comma);
            expression();
        }
    }

}
