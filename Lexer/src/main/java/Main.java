
import java.util.LinkedList;

import Lexical.LexicalAnalyzer;
import Lexical.Token;
import Syntax.ParserException;
import Syntax.SyntaxParser;

public class Main {
    public static void main(String[] args) {
        String SourceInputFile = "Lexer/test3.txt";
        String TokensOutputFile = "Lexer/test3_tokens.txt";
        String ASTOutputFile = "Lexer/test3_AST.txt";

        LexicalAnalyzer analyzer = new LexicalAnalyzer(SourceInputFile, TokensOutputFile);
        analyzer.start();
        for (Token token : analyzer.getTokens()) {
            System.out.println(token);
        }

        SyntaxParser parser = new SyntaxParser(analyzer.getTokens(), ASTOutputFile);
        
        try {
            parser.parse();
        } catch (ParserException e) {
            e.printStackTrace();
        }

        parser.getRoot().print("");
        
        /*
         * 存在的bug：
         * 声明语句必须在每段的开头（不知道是定义还是bug）
         */

    }
}
