
import java.io.IOException;
import java.util.LinkedList;
import Lexical.LexicalAnalyzer;
import Lexical.Token;
import Syntax.ASTNode;
import Syntax.TreePrinter;
import Syntax.ParserException;
import Syntax.SyntaxParser;

public class Main {
    public static void main(String[] args) throws ParserException, IOException {
        String SourceInputFile = "Lexer/TestCase/test6.txt";
        String TokensOutputFile = "Lexer/TestCase/tokens/test6_tokens.txt";
        String ASTOutputFile = "Lexer/TestCase/AST/test6_AST.txt";

        //构建词法分析器
        LexicalAnalyzer analyzer = new LexicalAnalyzer(SourceInputFile, TokensOutputFile);
        //开始词法分析
        analyzer.analysis();
        //获取词法分析器的token链表
        LinkedList<Token> tokens = analyzer.getTokens();
        //根据token列表构建语法分析器
        SyntaxParser parser = new SyntaxParser(tokens);   
        //开始语法分析
        parser.parse();
        //获取语法树根节点
        ASTNode root =  parser.getRoot();

        //输出语法树
        TreePrinter.print(ASTOutputFile,root);
    

    }
}
