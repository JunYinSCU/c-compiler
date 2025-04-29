
import java.util.LinkedList;
import Lexical.LexicalAnalyzer;
import Lexical.Token;
import Syntax.ASTNode;
import Syntax.TreePrinter1;
import Syntax.TreePrinter2;
import Syntax.ParserException;
import Syntax.SyntaxParser;

public class Main {
    public static void main(String[] args) throws ParserException {
        String SourceInputFile = "Lexer/test3.txt";
        String TokensOutputFile = "Lexer/test3_tokens.txt";
        String ASTOutputFile = "Lexer/test3_AST.txt";

        LexicalAnalyzer analyzer = new LexicalAnalyzer(SourceInputFile, TokensOutputFile);
        analyzer.start();

        LinkedList<Token> tokens = analyzer.getTokens();
        // for (Token token : tokens) {
        //     System.out.println(token);
        // }

        SyntaxParser parser = new SyntaxParser(tokens);
        

        parser.parse();
        

        ASTNode root =  parser.getRoot();
        //root.show();


        // int depth = TreePrinter1.getTreeDepth(root);
        // System.out.println("树的深度: " + depth);

        //TreePrinter1.show(root);
        TreePrinter2.print(ASTOutputFile,root);
        
        /*
         * 存在的bug：
         * 声明语句必须在每段的开头（不知道是定义还是bug）
         */

    }
}
