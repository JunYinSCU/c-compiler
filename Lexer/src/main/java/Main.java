
import java.util.LinkedList;
import Lexical.LexicalAnalyzer;
import Lexical.Token;
import Syntax.ASTNode;
import Syntax.TreePrinter;
import Syntax.ParserException;
import Syntax.SyntaxParser;

public class Main {
    public static void main(String[] args) throws ParserException {
        String SourceInputFile = "Lexer/test3.txt";
        String TokensOutputFile = "Lexer/test3_tokens.txt";
        String ASTOutputFile = "Lexer/test3_AST.txt";

        //构建词法分析器
        LexicalAnalyzer analyzer = new LexicalAnalyzer(SourceInputFile, TokensOutputFile);
        //开始词法分析
        analyzer.start();
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
        
        /*
         * 存在的bug：
         * 1. 声明语句必须在每段的开头（不知道是定义还是bug）
         * 2. input和output的定义不清楚,初步尝试将input和output的定义放在全局符号表中，
         */

    }
}
