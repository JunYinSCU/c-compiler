import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;



public class SyntaxParser {
    private LinkedList<Token> tokens;
    private Token EOFToken = new Token("EOF", "$", -1, -1);
    private int current = 0;

    private String inputFile = "input.txt";
    private String outputFile = "output.txt";
    private BufferedWriter output;

    public SyntaxParser(LinkedList<Token> tokens,String inputFile, String outputFile) {
        this.tokens = tokens;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        tokens.addLast(EOFToken);
        try {
            output = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public SyntaxParser(LinkedList<Token> tokens) {
        this.tokens = tokens;
        try {
            output = new BufferedWriter(new FileWriter(this.outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    * 将current指针向前移动一位，返回前一个token
    * 返回值：移动后的前一个token
    * */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /*
    * 判断是否到达tokens末尾
    * */
    private boolean isAtEnd() {
        return peek().getType() == EOFToken.getType();
    }

    /*
    * 获得当前token，指针不移动
    * */
    private Token peek() {
        return tokens.get(current);
    }

    /*
    * 获取前一个token，指针不移动
    * */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /*
    *
    * match方法用于判断当前token的类型是否与传入的token的类型相同
    * */
    private boolean match(Token t) {
        if (isAtEnd()) return false;
        return peek().getType() == t.getType();
    }

    private void consume(Token token, String message) {
        if (!match(token)) {
           error(token);
        }
    }

    private void error(Token token){
        String errorMessage = "Error: " + token.getValue() + " is not a valid token at line " + token.getRow() + ", column " + token.getColum();
        System.out.println(errorMessage);
    }
    /*
    * 主程序入口
    * */
    public void parse() {
        program();
    }

    private void program() {
        declaration_list();
    }

    private void declaration_list() {
        declaration();
        declaration_list1();
    }
    private void declaration_list1(){

    }
    private void declaration() {

    }
}
