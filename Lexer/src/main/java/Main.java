
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        src.main.java.Analyzer analyzer = new src.main.java.Analyzer(inputFile,outputFile);
        analyzer.start();

        LinkedList<Token> tokens = analyzer.getTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }

    }
}
