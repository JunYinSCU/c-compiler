import java.util.LinkedList;

public class main {
    public static void main(String[] args) {
        String input = "SyntaxParser\\input.txt";
        String output = "SyntaxParser\\output.txt";

        SyntaxParser parser = new SyntaxParser(input,output);
        parser.parse();

    }

        
}
