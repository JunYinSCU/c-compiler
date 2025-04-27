
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        String inputFile = "Lexer/test2.txt";
        String outputFile = "Lexer/test2_output.txt";

        Analyzer analyzer = new Analyzer(inputFile, outputFile);
        analyzer.start();
        for (Token token : analyzer.getTokens()) {
            System.out.println(token);
        }

        SyntaxParser parser = new SyntaxParser(analyzer.getTokens(), outputFile);
        parser.parse();
        /*
         * 存在的bug：
         * 无法识别数组下标 后面跟着除等号外其他字符的情况
         * 声明语句必须在每段的开头（不知道是定义还是bug）
         */


    }
}
