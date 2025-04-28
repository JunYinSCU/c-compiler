package Syntax;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import Lexical.Token;

public class SyntaxParser {
    private LinkedList<Token> tokens;   //token列表
    private Token EOFToken = new Token("EOF", "$", -1, -1);     // 文件结束符
    private int current = 0;    // 当前token指针
    private String outputFile = "SyntaxOutput.txt";     // 输出文件名
    private BufferedWriter output;
    private ASTNode root;   // 语法树根节点
    boolean hasError = false; // 是否存在语法错误

    public LinkedList<Token> getTokens() {
        return this.tokens;
    }

    public ASTNode getRoot() {
        return this.root;
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
    private ASTNode consume(Token expectedToken) throws ParserException {
        if (!match(expectedToken)) {
            String errorMessage = "Expected "+ expectedToken.getValue()+" at here";
            error(errorMessage,getCurrent());     
        }
        ASTNode node = new ASTNode(getCurrent().getValue());
        advance();  
        return node;   
    }


    private void error(String expected, Token currentToken) throws ParserException {
        String errorMessage = expected + "\n" +
                              "Current: " + currentToken.getValue() + " is not a valid token at line " +
                              currentToken.getRow() + ", column " + currentToken.getColum();
        throw new ParserException(errorMessage);
    }

    /*
     * 主程序入口
     */
    public void parse() throws ParserException{
        this.root = program();      
        System.out.println("Syntax analysis completed. No syntax errors found.");
    }

    /*
     * 文法1
     */
    private ASTNode program() throws ParserException{
        System.out.println("program");
        //创建AST根节点
        ASTNode programNode = new ASTNode("Program");

        ASTNode declarationListNode = declaration_list();
        programNode.addChild(declarationListNode);

        //语法分析完成
        //如果还有多余的token，则说明语法存在错误
        if(!isAtEnd()) {
            error("Unexpected tokens at the end of input: ",getCurrent());
        }

        return programNode;
    }

    /*
     * 文法2.1
     */
    private ASTNode declaration_list() throws ParserException{
        System.out.println("declaration_list");
        ASTNode declarationListNode = new ASTNode("declaration-list");

        ASTNode declarationNode = declaration();
        declarationListNode.addChild(declarationNode);

        LinkedList<ASTNode> declarationList= declaration_list1();
        for (ASTNode node : declarationList) {
            declarationListNode.addChild(node);
        }
        return declarationListNode;
    }

    /*
    * 文法2.2
    * 
    */
    private LinkedList<ASTNode> declaration_list1() throws ParserException{
        System.out.println("declaration_list1");
        LinkedList<ASTNode> declarations = new LinkedList<>();
        //var_declaration和fun_declaration类型定义实际都归结于type_specifier的int或者void
        while(isTypeSpecifier()) {
            declarations.add(declaration());
        }
        // ε 时不处理
        return declarations;
    }

    /*
    * 文法3
    * 
    */
    private ASTNode declaration() throws ParserException{
        System.out.println("declaration");
        ASTNode declarationNode = new ASTNode("declaration");

        if(isTypeSpecifier()){
            Token nextNext = lookAheadN(2); //获取第二个token判断是var还是fun            
            if(nextNext.getValue().equals(";") ||nextNext.getValue().equals("[")){
                ASTNode varDeclarationNode = var_declaration();
                declarationNode.addChild(varDeclarationNode);
            }else if(nextNext.getValue().equals( "(")){
                ASTNode funDeclarationNode = fun_declaration();
                declarationNode.addChild(funDeclarationNode);
            }
        }else{          
            error("Expected 'int' or 'void' at the beginning of a declaration", getCurrent());
        }

        return declarationNode;
    }

    /*
     * 文法4
     */
    private ASTNode var_declaration() throws ParserException{
        System.out.println("var_declaration");
        ASTNode varDeclarationNode = new ASTNode("var-declaration");

        ASTNode typeSpecifierNode = type_specifier();
        varDeclarationNode.addChild(typeSpecifierNode);

        //对终结符进行匹配消耗
        varDeclarationNode.addChild(consume(new Token("ID","null")));

        if(match(new Token("SEPARATOR","["))){
            varDeclarationNode.addChild(consume(new Token("SEPARATOR","[")));
            varDeclarationNode.addChild(consume(new Token("NUM","null")));
            varDeclarationNode.addChild(consume(new Token("SEPARATOR","]")));
        }

        varDeclarationNode.addChild(consume(new Token("SEPARATOR",";")));

        return varDeclarationNode;
    }

    /*
     * 文法5
     */
    private ASTNode type_specifier()throws ParserException{
        System.out.println("type_specifier");
        ASTNode typeSpecifierNode = new ASTNode("type-specifier");

        Token INTtoken = new Token("KEYWORD","int");
        Token VOIDtoken = new Token("KEYWORD","void");

        if(match(INTtoken)){
            typeSpecifierNode.addChild(consume(INTtoken));
        }else if(match(VOIDtoken)){
            typeSpecifierNode.addChild(consume(VOIDtoken));
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Expected 'int' or 'void'",getCurrent());
            advance();
        }
        return typeSpecifierNode;
    }

    private boolean isTypeSpecifier(){
        Token INTtoken = new Token("KEYWORD","int");
        Token VOIDtoken = new Token("KEYWORD","void");
        return match(INTtoken) || match(VOIDtoken);
    }

    /*
     * 文法6
     */
    private ASTNode fun_declaration()throws ParserException {
        System.out.println("fun_declaration");
        ASTNode funDeclarationNode = new ASTNode("fun-declaration");

        funDeclarationNode.addChild(type_specifier());

        funDeclarationNode.addChild(consume(new Token("ID","null")));
        funDeclarationNode.addChild(consume(new Token("SEPARATOR","(")));
        funDeclarationNode.addChild(params());
        funDeclarationNode.addChild(consume(new Token("SEPARATOR",")")));
        funDeclarationNode.addChild(compound_stmt());

        return funDeclarationNode;
    }
    
    /*
     * 文法7
     */
    private ASTNode params()throws ParserException{
        System.out.println("params");
        ASTNode paramsNode = new ASTNode("params");

        Token voidToken = new Token("KEYWORD","void");
        if(match(voidToken)){
            paramsNode.addChild(consume(voidToken));
        }else{
            paramsNode.addChild(param_list());
        }

        return paramsNode;
    }

    /*
     * 文法8.1
     */
    private ASTNode param_list()throws ParserException{
        System.out.println("param_list");
        ASTNode paramListNode = new ASTNode("param-list");
        paramListNode.addChild(param());

        LinkedList<ASTNode> paramList = param_list1();
        for (ASTNode node : paramList) {
            paramListNode.addChild(node);
        }

        return paramListNode;
    }

    /*
     * 文法8.2
     */
    private LinkedList<ASTNode> param_list1()throws ParserException{
        System.out.println("param_list1");
        LinkedList<ASTNode> paramList = new LinkedList<>();
        Token commaToken = new Token("SEPARATOR",",");
        while(match(commaToken)){
            paramList.add(consume(commaToken));
            paramList.add(param());
        }
        // ε 时不处理
        return paramList;
    }

    /*
     * 文法9
     */
    private ASTNode param()throws ParserException{
        System.out.println("param");
        ASTNode paramNode = new ASTNode("param");

        paramNode.addChild(type_specifier());

        Token ID = new Token("ID","null");       
        paramNode.addChild(consume(ID));

        Token leftBracket = new Token("SEPARATOR","[");
        if(match(leftBracket)){
            paramNode.addChild(consume(leftBracket));
            paramNode.addChild(consume(new Token("SEPARATOR","]")));
        }

        return paramNode;
    }

    /*
     * 文法10
     */
    private ASTNode compound_stmt()throws ParserException{
        System.out.println("compound_stmt");
        ASTNode compoundStmtNode = new ASTNode("compound-stmt");

        compoundStmtNode.addChild(consume(new Token("SEPARATOR","{")));
        compoundStmtNode.addChild(local_declarations());
        compoundStmtNode.addChild(statement_list());
        compoundStmtNode.addChild(consume(new Token("SEPARATOR","}")));

        return compoundStmtNode;
    }

    /*
     * 文法11.1
     * 
     */
    private ASTNode local_declarations()throws ParserException{
        System.out.println("local_declarations");
        ASTNode localDeclarationsNode = new ASTNode("local-declarations");

        LinkedList<ASTNode> list = local_declarations1();
        for (ASTNode node : list) {
            localDeclarationsNode.addChild(node);
        }

        return localDeclarationsNode;
    }

    /*
     * 文法11.2
     * 
     */
    private LinkedList<ASTNode> local_declarations1()throws ParserException{
        System.out.println("local_declarations1");
        LinkedList<ASTNode> localDeclarations = new LinkedList<>();
        while(isTypeSpecifier()){
            localDeclarations.add(var_declaration());
        }
        // ε 时不处理

        return localDeclarations;
    }

    /*
     * 文法12.1
     * 
     */
    private ASTNode statement_list()throws ParserException{
        System.out.println("statement_list");
        ASTNode statementListNode = new ASTNode("statement-list");

        LinkedList<ASTNode> statementList = statement_list1();
        for (ASTNode node : statementList) {
            statementListNode.addChild(node);
        }

        return statementListNode;
    }

    /*
     * 文法12.2
     * 
     */
    private LinkedList<ASTNode> statement_list1()throws ParserException{
        System.out.println("statement_list1");
        LinkedList<ASTNode> statementList = new LinkedList<>();
        while(isStatement()){
            statementList.add(statement());
        }
        // ε 时不处理

        return statementList;
    }

    private boolean isStatement() {
        return isExpressionStmt() || isCompoundStmt() || isSelectionStmt() ||
                isIterationStmt() || isReturnStmt();
    }

    /*
     * 文法13
     * 为expression_stmt、compound_stmt、selection_stmt、iteration_stmt、return_stmt时进入statement
     */
    private ASTNode statement() throws ParserException{
        System.out.println("statement");
        ASTNode statementNode = new ASTNode("statement");

        if(!isAtEnd()){
            if(isExpressionStmt()){
                statementNode.addChild(expression_stmt());
            }else if(isCompoundStmt()){
                statementNode.addChild(compound_stmt());
            }else if(isSelectionStmt()){
                statementNode.addChild(selection_stmt());
            }else if(isIterationStmt()){
                statementNode.addChild(iteration_stmt());
            }else if(isReturnStmt()){
                statementNode.addChild(return_stmt());
            }else{
                //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
                error("Unexpected token at the start of a statement.",getCurrent());
                advance();
            }
        }else{
            error("Expected a statement but found end of input", getCurrent());
        }

        return statementNode;
        
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
    private ASTNode expression_stmt() throws ParserException{
        System.out.println("expression_stmt");
        ASTNode expressionStmtNode = new ASTNode("expression-stmt");
        
        if(isExpression()){
            expressionStmtNode.addChild(expression());
        } 

        expressionStmtNode.addChild(consume(new Token("SEPARATOR",";")));

        return expressionStmtNode;
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
    private ASTNode selection_stmt()throws ParserException{
        System.out.println("selection_stmt");
        ASTNode selectionStmtNode = new ASTNode("selection-stmt");

        selectionStmtNode.addChild(consume(new Token("KEYWORD","if")));
        selectionStmtNode.addChild(consume(new Token("SEPARATOR","(")));
        selectionStmtNode.addChild(expression());
        selectionStmtNode.addChild(consume(new Token("SEPARATOR",")")));
        selectionStmtNode.addChild(statement());

        if(match(new Token("KEYWORD","else"))){
            selectionStmtNode.addChild(consume(new Token("KEYWORD","else")));
            selectionStmtNode.addChild(statement());
        }

        return selectionStmtNode;
    }

    /*
     * 文法16
     * 
     */
    private ASTNode iteration_stmt()throws ParserException{
        System.out.println("iteration_stmt");
        ASTNode iterationStmtNode = new ASTNode("iteration-stmt");

        iterationStmtNode.addChild(consume(new Token("KEYWORD","while")));
        iterationStmtNode.addChild(consume(new Token("SEPARATOR","(")));
        iterationStmtNode.addChild(expression());
        iterationStmtNode.addChild(consume(new Token("SEPARATOR",")")));
        iterationStmtNode.addChild(statement());

        return iterationStmtNode;
    }

    /*
     * 文法17
     * 
     */
    private ASTNode return_stmt()throws ParserException{
        System.out.println("return_stmt");
        ASTNode returnStmtNode = new ASTNode("return-stmt");

        returnStmtNode.addChild(consume(new Token("KEYWORD","return")));
        if(!match(new Token("SEPARATOR",";"))){
            returnStmtNode.addChild(expression());
        }
        returnStmtNode.addChild(consume(new Token("SEPARATOR",";")));

        return returnStmtNode;
    }

    /*
     * 文法18
     * 为var或者simple_expression时进入expression
     * expression文法定义较为复杂
     */
    private ASTNode expression()throws ParserException{
        System.out.println("expression");
        ASTNode expressionNode = new ASTNode("expression");

        // 先保存当前Token位置，方便回溯
        int savePos = current;

        if (isVar()) {
            // 先尝试识别var
            expressionNode.addChild(var());

            // 看下一个Token
            Token next = getCurrent();
            if (isEqual(next, new Token("OPERATOR", "="))) {
                // 确认是赋值表达式 var = expression
                expressionNode.addChild(consume(new Token("OPERATOR", "=")));
                expressionNode.addChild(expression());
                return expressionNode;
            } else {
                // 不是赋值，回溯到var前的位置，识别simple-expression
                current = savePos;
            }
        }

        // 否则走 simple-expression
        expressionNode.addChild(simple_expression());  

        return expressionNode;
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
    private ASTNode var() throws ParserException{
        System.out.println("var");
        ASTNode varNode = new ASTNode("var");

        varNode.addChild(consume(new Token("ID","null")));

        if(match(new Token("SEPARATOR","["))){
            varNode.addChild(consume(new Token("SEPARATOR","[")));
            varNode.addChild(expression());
            varNode.addChild(consume(new Token("SEPARATOR","]")));
        }

        return varNode;
    }

    /*
     * 文法20
     * 第一个为term时进入simple_expression
     */
    private ASTNode simple_expression() throws ParserException{
        System.out.println("simple_expression");
        ASTNode simpleExpressionNode = new ASTNode("simple-expression");

        simpleExpressionNode.addChild(additive_expression());

        if (isRelop()) {
            simpleExpressionNode.addChild(relop());
            simpleExpressionNode.addChild(additive_expression());
        }

        return simpleExpressionNode;
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
    private ASTNode relop() throws ParserException{
        System.out.println("relop");
        ASTNode relopNode = new ASTNode("relop");

        if(isRelop()){
            ASTNode relop = new ASTNode(getCurrent().getValue());
            relopNode.addChild(relop);
            advance();
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Expected relop e.g. <= < >= > ... here",getCurrent());
            advance();
        }

        return relopNode;
    }

    /*
     * 文法22.1
     * 
     */
    private ASTNode additive_expression() throws ParserException{
        System.out.println("additive_expression");
        ASTNode additiveExpressionNode = new ASTNode("additive-expression");

        additiveExpressionNode.addChild(term());

        LinkedList<ASTNode> list = additive_expression1();
        for (ASTNode node : list) {
            additiveExpressionNode.addChild(node);
        }
        return additiveExpressionNode;
    }

    /*
     * 文法22.2
     * 
     */
    private LinkedList<ASTNode> additive_expression1()throws ParserException {
        System.out.println("additive_expression1");
        LinkedList<ASTNode> additiveExpressionList = new LinkedList<>();

        while(isAddop()){
            additiveExpressionList.add(addop());
            additiveExpressionList.add(term());
        }

        return additiveExpressionList;
        
    }

    private boolean isAddop() {
        // + -
        return match(new Token("OPERATOR", "+")) || match(new Token("OPERATOR", "-"));
    }

    /*
     * 文法23
     * 
     */
    private ASTNode addop()throws ParserException{
        System.out.println("addop");
        ASTNode addopNode = new ASTNode("addop");

        if(isAddop()){
            ASTNode addop = new ASTNode(getCurrent().getValue());
            addopNode.addChild(addop);
            advance();
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Expected '+' or '-' here",getCurrent());
            advance();
        }

        return addopNode;
    }

    /*
     * 文法24.1
     * 为factor时进入term
     */
    private ASTNode term() throws ParserException{
        System.out.println("term");
        ASTNode termNode = new ASTNode("term");

        termNode.addChild(factor());

        LinkedList<ASTNode> termList = term1();
        for (ASTNode node : termList) {
            termNode.addChild(node);
        }

        return termNode;
    }

    /*
     * 文法24.2
     * 
     */
    private LinkedList<ASTNode> term1() throws ParserException{
        System.out.println("term1");
        LinkedList<ASTNode> termList = new LinkedList<>();

        while(isMulop()){
            termList.add(mulop());
            termList.add(factor());
        }

        return termList;
    }

    private boolean isMulop() {
        // * /
        return match(new Token("OPERATOR", "*")) || match(new Token("OPERATOR", "/"));
    }

    /* 
     * 文法25
     */
    private ASTNode mulop()throws ParserException{
        System.out.println("mulop");
        ASTNode mulopNode = new ASTNode("mulop");

       if(isMulop()){
            ASTNode mulop = new ASTNode(getCurrent().getValue());
            mulopNode.addChild(mulop);
            advance();
        }else{
            //如果都不匹配，则说明当前是错误的token,提示错误并向前移动
            error("Expected '*' or '/' here ",getCurrent());
            advance();
        }

        return mulopNode;
    }

    /*
     * 文法26
     * 为( ID  NUM时进入factor 
     */
    private ASTNode factor() throws ParserException{
        System.out.println("factor");
        ASTNode factorNode = new ASTNode("factor");

        Token leftBracket = new Token("SEPARATOR", "(");
        Token ID = new Token("ID", "null");
        Token NUM = new Token("NUM", "null");

        if(!isAtEnd()){
            if (match(leftBracket)) {   //匹配 ( , 则为(expression)的情况
                factorNode.addChild(consume(leftBracket));
                factorNode.addChild(expression());
                factorNode.addChild(consume(new Token("SEPARATOR", ")")));
            } else if (match(ID)) {     //匹配ID，可能是函数调用或者变量
                Token leftBracket1 = lookAheadN(1);     //继续获得下一个token
                if(isEqual(leftBracket,leftBracket1)){      //如果下一个token为(，则为函数调用
                    factorNode.addChild(call());
                }else{      //否则为变量
                    factorNode.addChild(var());
                }
            } else if (match(NUM)) {    //匹配NUM，直接消耗
                factorNode.addChild(consume(NUM));
            } else {
                // 如果都不匹配，则说明当前是错误的token,提示错误并向前移动
                error("Expected '(expression)', ID, call func or NUM as a factor here",getCurrent());
                advance();
            }
        }else{
            error("Expected a factor but found end of input", getCurrent());
        }

        return factorNode;
        
    }

    /*
     * 文法27
     * 
     */
    private ASTNode call() throws ParserException{
        System.out.println("call");
        ASTNode callNode = new ASTNode("call");

        callNode.addChild(consume(new Token("ID","null")));
        callNode.addChild(consume(new Token("SEPARATOR","(")));
        callNode.addChild(args());
        callNode.addChild(consume(new Token("SEPARATOR",")")));

        return callNode;
    }

    /*
     * 文法28
     * 
     */
    private ASTNode args() throws ParserException{
        System.out.println("args");
        ASTNode argsNode = new ASTNode("args");
        if(isExpression()){
            argsNode.addChild(args_list());
        }

        return argsNode;
             
    }

    /*
     * 文法29.1
     * 
     */
    private ASTNode args_list() throws ParserException{
        System.out.println("args_list");
        ASTNode argsListNode = new ASTNode("args-list");
        argsListNode.addChild(expression());

        LinkedList<ASTNode> list  = args_list1();
        for (ASTNode node : list) {
            argsListNode.addChild(node);
        }
        return argsListNode;
    }

    /*
     * 文法29.2
     * 
     */
    private LinkedList<ASTNode> args_list1() throws ParserException{
        System.out.println("args_list1");
        LinkedList<ASTNode> argsList = new LinkedList<>();

        Token comma = new Token("SEPARATOR", ",");

        while (match(comma)) {
            argsList.add(consume(comma));
            argsList.add(expression());
        }

        return argsList;
    }

}
