import java.util.LinkedList;

public class main {
    public static void main(String[] args) {
        // Create a new instance of the SyntaxParser class
        SyntaxParser parser = new SyntaxParser();
        LinkedList<Token> tokens = parser.getTokens();
        // Print the tokens to the console
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

        
}
