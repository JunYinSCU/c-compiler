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

        if(t.getValue() == "null"){
            if(peek().getType() == t.getType()){
                return true;
            }
        }else{
            if(peek().getType() == t.getType() && peek().getValue() == t.getValue()){
                return true;
            }
        }
        return false;
    }
    private boolean match(String type) {
        if (isAtEnd())
            return false;
        return peek().getType() == type;
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

        if(){
            var_declaration();
        }else if(){
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
     * 文法11
     * 
     */
    private void local_declarations(){
        //todo：statement_list的实现
    }

    /*
     * 文法12
     * 
     */
    private void statement_list(){
        //todo：statement_list的实现
    }
}
