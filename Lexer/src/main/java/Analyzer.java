
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;


public class Analyzer {
	private LinkedList<Character> lexemeBuffer = new LinkedList<>();	//存储当前词素
	private LinkedList<Token> tokens = new LinkedList<>();	//存储token列表
	private String currentLine;		//当前处理的行
	private BufferedWriter output;	//输出
	private StringBuilder commentBuffer = new StringBuilder();	//注释
	private String inputFile = "input.txt";		//输入文件名，默认为input.txt
	private String outputFile = "output.txt";	//输出文件名，默认为output.txt
	private int row = 0;		//用于记录当前行号
	private int column = 0;		//用于记录当前列号
	private int commentRow = 0;		//用于记录注释开始行号
	private int commentColumn = 0;		//用于记录注释开始列号

	public LinkedList<Token> getTokens() {
		return tokens;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public Analyzer(String inputFile, String outputFile){	//构造函数，定义输入输出文件，创建输出文件
		this.inputFile = inputFile;
		this.outputFile = outputFile;

		try {
			output = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Analyzer() {	//用默认的输入输出文件，创建输出文件
		try {
			output = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean inMultilineComment = false;		//判断是否是多行注释
	private static final int STATE_INITIAL = 0;		//初始状态
	private static final int STATE_IDENTIFIER = 1;	//关键词或者标识符
	private static final int STATE_IDENTIFIER_END = 2;	//关键词或者标识符结束
	private static final int STATE_INTEGER = 3;			//整数
	private static final int STATE_INTEGER_END = 4; 	//整数结束
	private static final int STATE_PLUS = 5;		// +
	private static final int STATE_MINUS = 6;		// -
	private static final int STATE_MULTIPLY = 7;	// *
	private static final int STATE_DIVIDE = 8;		// /
	private static final int STATE_SEMICOLON = 9;	// ;
	private static final int STATE_COMMA = 10;		// ,
	private static final int STATE_EQUAL_START = 11;	// =
	private static final int STATE_EQUAL_END = 12;		
	private static final int STATE_GREAT_START = 13;	// >
	private static final int STATE_GREAT_EQUAL = 14;	// >=
	private static final int STATE_LESS_START = 16;		// <
	private static final int STATE_LESS_EQUAL = 17;		// <=
	private static final int STATE_NOT_EQUAL = 18;		// !=
	private static final int STATE_LEFT_PARENTHESIS = 19;	// (
	private static final int STATE_RIGHT_PARENTHESIS = 20;	// )
	private static final int STATE_LEFT_BRACE = 21;			// {
	private static final int STATE_RIGHT_BRACE = 22;		// }
	private static final int STATE_LEFT_BRACKET = 23;		// [
	private static final int STATE_RIGHT_BRACKET = 24;		// ]
	private static final int STATE_IN_COMMENT = 25;			// 注释
	private static final int STATE_COMMENT_END_STAR = 26;	// 注释结束

	private void putCharToLexemeBuffer(char c) {  //将字符放入lexemeBuffer
		lexemeBuffer.offerLast(c);
	}

	private String getLexemeBuffer() {		//获取当前词素
		StringBuilder sb = new StringBuilder();
		while (!lexemeBuffer.isEmpty()) {
			sb.append(lexemeBuffer.poll());
		}
		return sb.toString();
	}

	private void outPutToken(Token token) throws IOException {	//将token添加到列表，输出到文件中
		tokens.add(token);
		//System.out.println(token);
		output.write(token.toString());
		output.newLine();
	}

	public void start(){	//词法分析器的入口函数
		getText();
	}

	private void getText() {	//获取每一行进行分析
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(inputFile));

			while ((currentLine = bufferedReader.readLine()) != null) {
				row++;			//新行，则行号增加
				Analysis();		//对这一行进行词法分析
			}
		}catch (Exception e) {

		} finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private void Analysis() throws IOException {
		int state;	//用于判断当前状态

		if(inMultilineComment){		//如果是在多行注释的状态，则起始状态为STATE_IN_COMMENT
			state = STATE_IN_COMMENT;
		}else{
			state = STATE_INITIAL;	//否则为初始状态STATE_INITIAL
		}

		//column为当前列号，用于读取当前处理字符
		//注意：需要是<=当前长度，如果为<，则会出现最后一个字符无法处理的情况
		int index = 0;
		while (index <= currentLine.length()) {	
			char c = (index < currentLine.length()) ? currentLine.charAt(index) : (char) -1;	//如果到了最后，用一个特殊字符来标识

			switch (state) {
				case STATE_INITIAL:
					if (Character.isLetter(c)) {
						column = index;					
						state = STATE_IDENTIFIER;
						putCharToLexemeBuffer(c);	//如果是字母，则进入标识符状态，将字符放入lexemeBuffer
					} else if (Character.isDigit(c)) {	
						column = index;			
						state = STATE_INTEGER;
						putCharToLexemeBuffer(c);	//数字同理
					} else if (c == '+') {	
						column = index;					
						state = STATE_PLUS;
					} else if (c == '-') {	
						column = index;			
						state = STATE_MINUS;
					} else if (c == '*') {	
						column = index;						
						state = STATE_MULTIPLY;
					} else if (c == '/') {	
						column = index;						
						state = STATE_DIVIDE;
					} else if (c == ';') {	
						column = index;					
						state = STATE_SEMICOLON;
					} else if (c == ',') {	
						column = index;					
						state = STATE_COMMA;
					} else if (c == '=') {	
						column = index;					
						state = STATE_EQUAL_START;
					} else if (c == '>') {
						column = index;	
						state = STATE_GREAT_START;
					} else if (c == '<') {
						column = index;	
						state = STATE_LESS_START;
					} else if (c == '!') {
						column = index;	
						state = STATE_NOT_EQUAL;
					} else if (c == '(') {
						column = index;	
						state = STATE_LEFT_PARENTHESIS;
					} else if (c == ')') {
						column = index;	
						state = STATE_RIGHT_PARENTHESIS;
					} else if (c == '{') {
						column = index;	
						state = STATE_LEFT_BRACE;
					} else if (c == '}') {
						column = index;	
						state = STATE_RIGHT_BRACE;
					} else if (c == '[') {
						column = index;	
						state = STATE_LEFT_BRACKET;
					} else if (c == ']') {	
						column = index;						
						state = STATE_RIGHT_BRACKET;
					} else {
						if (index == currentLine.length()) {	//如果已经处理完最后一个字符了，那么直接跳过即可，序号++
							index++;
							break;
						}
						if (c != ' ') {		//如果不是空格，则无法识别，是非法字符。
							System.out.println("非法字符:"+c);
							output.write("非法字符:"+c);
							output.newLine();
							lexemeBuffer.clear();
						}
					}
					index++;	//处理下一个字符
					break;

				case STATE_IDENTIFIER:
					if (Character.isLetter(c) || Character.isDigit(c)) {	//标识符必须是字母开头，但可以跟数字
						putCharToLexemeBuffer(c);
						state = STATE_IDENTIFIER;	//继续当前状态
						index++;	//处理下一个字符
					} else {	//如果不是字母数字，则说明标识符结束了，改变状态，但不要index++，因为当前的index处字符因不符合，还未识别处理
						state = STATE_IDENTIFIER_END;
					}
					break;

				case STATE_IDENTIFIER_END:
					String identifier = getLexemeBuffer();
					if (IDKeyword.isKeywords(identifier.trim())) {	//判断是关键字还是标识符
						Token token = new Token("KEYWORD", identifier,row,column);
						//创建token，row和column即为当前token的位置，随后进行输出
						outPutToken(token);
					} else {
						Token token = new Token("ID", identifier.trim(), row, column);
						outPutToken(token);
					}
					lexemeBuffer.clear();	//清除当前队列，避免影响后续分析
					state = STATE_INITIAL;	//改为初始状态
					break;

				case STATE_INTEGER:
					//和标识符处理逻辑相同
					if (Character.isDigit(c)) {
						putCharToLexemeBuffer(c);
						state = STATE_INTEGER;
						index++;
					} else {
						state = STATE_INTEGER_END;
					}
					break;

				case STATE_INTEGER_END:
					//和标识符处理逻辑相同
					String intStr = getLexemeBuffer();
					Token token = new Token("NUM", intStr.trim(),row,column);
					outPutToken(token);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_PLUS:
					//如果是各种操作符和分隔符，直接输出，没必要加入到lexemeBuffer中，注意也不要index增加
					Token plusToken = new Token("OPERATOR_PLUS", "+", row, column);
					outPutToken(plusToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_MINUS:
					Token minusToken = new Token("OPERATOR_MINUS", "-", row,column);
					outPutToken(minusToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_MULTIPLY:
					Token multiplyToken = new Token("OPERATOR_MULTIPLY", "*", row,column);
					outPutToken(multiplyToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_DIVIDE:
					if (c == '*') {		//如果是 /*，则说明这是注释符号
						commentRow = row;	//记录注释开始的行列号
						commentColumn = column;
						state = STATE_IN_COMMENT;	//改变状态
						commentBuffer = new StringBuilder();
						index++;	//处理下一个字符，即为注释文本
					} else {	//否则上一个字符就是单纯除法，直接处理即可，不要index++，因为还未识别当前字符具体类型
						Token divideToken = new Token("OPERATOR_DIVIDE", "/", row, column);
						outPutToken(divideToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;

				case STATE_SEMICOLON:
					Token semicolonToken = new Token("SEPARATOR_SEMICOLON", ";", row,column);
					outPutToken(semicolonToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_COMMA:
					Token commaToken = new Token("SEPARATOR_COMMA", ",", row,column);
					outPutToken(commaToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_EQUAL_START:
					if (c == '=') {		//说明当前符号为==,改变状态，读取下一个字符
						state = STATE_EQUAL_END;
						index++;
					} else {	//否则上一个字符就是简单的=，直接处理，不要index++，因为还没识别当前字符具体类型
						Token equalToken = new Token("OPERATOR_ASSIGN", "=", row,column);
						outPutToken(equalToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;

				case STATE_EQUAL_END:
					Token equalEndToken = new Token("OPERATOR_EQUAL", "==", row,column);
					outPutToken(equalEndToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_GREAT_START:
					//同=和==的判断逻辑
					if (c == '=') {
						state = STATE_GREAT_EQUAL;
						index++;
					} else {
						Token greatToken = new Token("OPERATOR_GREATER", ">", row,column);
						outPutToken(greatToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;

				case STATE_GREAT_EQUAL:
					Token greatEqualToken = new Token("OPERATOR_GREATER_EQUAL", ">=", row,column);
					outPutToken(greatEqualToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_LESS_START:
					if (c == '=') {
						state = STATE_LESS_EQUAL;
						index++;
					} else {
						Token lessToken = new Token("OPERATOR_LESS", "<", row,column);
						outPutToken(lessToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;

				case STATE_LESS_EQUAL:
					Token lessEqualToken = new Token("OPERATOR_LESS_EQUAL", "<=", row,column);
					outPutToken(lessEqualToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_NOT_EQUAL:
					if (c == '=') {
						Token notEqualToken = new Token("OPERATOR_NOT_EQUAL", "!=", row,column);
						outPutToken(notEqualToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;

				case STATE_LEFT_PARENTHESIS:
					Token leftParenthesisToken = new Token("SEPARATOR_LEFT_PARENTHESIS", "(", row,column);
					outPutToken(leftParenthesisToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_RIGHT_PARENTHESIS:
					Token rightParenthesisToken = new Token("SEPARATOR_RIGHT_PARENTHESIS", ")", row,column);
					outPutToken(rightParenthesisToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_LEFT_BRACE:
					Token leftBraceToken = new Token("SEPARATOR_LEFT_BRACE", "{", row,column);
					outPutToken(leftBraceToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_RIGHT_BRACE:
					Token rightBraceToken = new Token("SEPARATOR_RIGHT_BRACE", "}", row,column);
					outPutToken(rightBraceToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_LEFT_BRACKET:
					Token leftBracketToken = new Token("SEPARATOR_LEFT_BRACKET", "[", row,column);
					outPutToken(leftBracketToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_RIGHT_BRACKET:
					Token rightBracketToken = new Token("SEPARATOR_RIGHT_BRACKET", "]", row,column);
					outPutToken(rightBracketToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;

				case STATE_IN_COMMENT:
					//进入注释状态，改变标志位true
					inMultilineComment = true;
					if (c == '*') {		//如果在注释状态遇到*,则可能是*/结束标志
						state = STATE_COMMENT_END_STAR;
					} else if (c == (char) -1) {	//如果到了行尾，还是当前注释状态，说明注释还未结束，添加换行符
						commentBuffer.append("\\n ");
						state = STATE_IN_COMMENT;
					} else {
						commentBuffer.append(c);	//否则就是正常注释文本，添加到注释buffer中
					}
					index++;	//处理下一个字符
					break;

				case STATE_COMMENT_END_STAR:
					if (c == '/') {		//前置字符时*,如果这个字符是/则表明注释结束
						// 注释结束，输出Token
						Token commentToken = new Token("COMMENT", commentBuffer.toString().trim(), commentRow, commentColumn);
						outPutToken(commentToken);
						lexemeBuffer.clear();
						commentBuffer.setLength(0);	//清空注释文本
						state = STATE_INITIAL;
						inMultilineComment = false;
						index++;
					} else if (c == '*') {	//如果是连续星号，则继续判断下一个字符是不是/
						commentBuffer.append(c);
						state = STATE_COMMENT_END_STAR;
						index++;
					} else {	//如果当前字符不是/ 或者 *，则说明前置的*是注释文本的一部分，将前置的*和当前字符都要加入
						commentBuffer.append('*').append(c);
						state = STATE_IN_COMMENT;
						index++;
					}
					break;
			}
		}
	}

}
