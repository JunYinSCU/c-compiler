
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        String inputFile = "Lexer/test.txt";
        String outputFile = "Lexer/test_output.txt";

        Analyzer analyzer = new Analyzer(inputFile, outputFile);
        analyzer.start();

        // LinkedList<Token> tokens = analyzer.getTokens();
        // for (Token token : tokens) {
        //     System.out.println(token);
        // }
        SyntaxParser parser = new SyntaxParser(analyzer.getTokens(), outputFile);
        parser.parse();


    }
}
