import java.util.LinkedList;

public class main {
    public static void main(String[] args) {
        String input = "SyntaxParser/input.txt";
        String output = "SyntaxParser/output.txt";
        
        SyntaxParser parser = new SyntaxParser(input,output);
        parser.parse();

    }

    /*
     * 当前问题：
     * 1. 赋值语句后面跟声明语句时报错
     * 2. 函数无法接受多个参数
     */

}
