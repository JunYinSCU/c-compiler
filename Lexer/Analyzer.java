import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;



public class Analyzer {
	private String currentLine;
	private LinkedList<Character> sourceChar = new LinkedList<Character>();
	private LinkedList<Character> lexemeBuffer = new LinkedList<Character>();
	private BufferedWriter output;

	private static final int STATE_INITIAL = 0;
	private static final int STATE_IDENTIFIER = 1;
	private static final int STATE_IDENTIFIER_END = 2;
	private static final int STATE_INTEGER = 3;
	private static final int STATE_INTEGER_END = 4;
	private static final int STATE_PLUS = 5;
	private static final int STATE_MINUS = 6;
	private static final int STATE_MULTIPLY = 7;
	private static final int STATE_DIVIDE = 8;
	private static final int STATE_SEMICOLON = 9;
	private static final int STATE_COMMA = 10;
	private static final int STATE_EQUAL_START = 11;
	private static final int STATE_EQUAL_END = 12;
	private static final int STATE_GREAT_START = 13;
	private static final int STATE_GREAT_EQUAL = 14;
	private static final int STATE_LESS_START = 16;
	private static final int STATE_LESS_EQUAL = 17;
	private static final int STATE_NOT_EQUAL = 18;
	private static final int STATE_LEFT_PARENTHESIS = 19;
	private static final int STATE_RIGHT_PARENTHESIS = 20;
	private static final int STATE_LEFT_BRACE = 21;
	private static final int STATE_RIGHT_BRACE = 22;
	private static final int STATE_LEFT_BRACKET = 23;
	private static final int STATE_RIGHT_BRACKET = 24;

	public Analyzer() {
		try {
			output = new BufferedWriter(new FileWriter("output.txt"));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public char getSourceChar(){
		return sourceChar.peekFirst();   //获取sourceChar的第一个字符
	}

	public void putCharToLexemeBuffer(char c) { //将字符放入lexemeBuffer的末尾
		lexemeBuffer.offerLast(c);
	}
	public String getLexemeBuffer() {       //获取lexemeBuffer的内容
		String string = new String();
		for (int i = 0; i < lexemeBuffer.size(); i++) {
			char c = lexemeBuffer.get(i);
			string += c;
		}
		return string;
	}

	public void outPutToken(Token token) throws IOException {
		token.display();
		output.write(token.toString());
		output.newLine();
	}

	public void outPutToken(String type, String value,String location) throws IOException {
		Token token = new Token(type,value, location);
		token.display();
		output.write(token.toString());
		output.newLine();
	}

	public void getText() {                
		char a[];
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader("input.txt"));
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				currentLine = str;
				currentLine.replaceAll("\\s+", "");// 去掉一个以上的空白符，用一个空白代替
				a = currentLine.toCharArray();
				for (char c : a) {
					sourceChar.offer(c);
				}
				analysis();
			}
		} catch (Exception e) {
			
		} finally { // 关闭资源
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void Analysis() throws IOException{
		int state = STATE_INITIAL;

		while(!sourceChar.isEmpty()){
			char c = getSourceChar();
			switch(state){
				case STATE_INITIAL:
					if(Character.isLetter(c)){
						state = STATE_IDENTIFIER;
						putCharToLexemeBuffer(sourceChar.pollFirst());
					}else if(Character.isDigit(c)){
						state = STATE_INTEGER;
						putCharToLexemeBuffer(sourceChar.pollFirst());
					}else if(c == '+'){
						sourceChar.pollFirst();
						state = STATE_PLUS;
					}else if(c == '-'){
						sourceChar.pollFirst();
						state = STATE_MINUS;
					}else if(c == '*'){
						sourceChar.pollFirst();
						state = STATE_MULTIPLY;
					}else if(c == '/'){
						sourceChar.pollFirst();
						state = STATE_DIVIDE;
					}else if(c == ';'){
						sourceChar.pollFirst();
						state = STATE_SEMICOLON;
					}else if(c == ','){
						sourceChar.pollFirst();
						state = STATE_COMMA;
					}else if(c == '='){
						sourceChar.pollFirst();
						state = STATE_EQUAL_START;
					}else if(c == '>'){
						sourceChar.pollFirst();
						state = STATE_GREAT_START;
					}else if(c == '<'){
						sourceChar.pollFirst();
						state = STATE_LESS_START;
					}else if(c == '!'){
						sourceChar.pollFirst();
						state = STATE_NOT_EQUAL;
					}else if(c == '('){
						sourceChar.pollFirst();
						state = STATE_LEFT_PARENTHESIS;
					}else if(c == ')'){
						sourceChar.pollFirst();
						state = STATE_RIGHT_PARENTHESIS;
					}else if(c == '{'){
						sourceChar.pollFirst();
						state = STATE_LEFT_BRACE;
					}else if(c == '}'){	
						sourceChar.pollFirst();
						state = STATE_RIGHT_BRACE;
					}else if(c == '['){
						sourceChar.pollFirst();
						state = STATE_LEFT_BRACKET;
					}else if(c == ']'){
						sourceChar.pollFirst();
						state = STATE_RIGHT_BRACKET;
					}else {
						if (c != ' ') {
							System.out.print("不能识别");
							output.write("不能识别");
							output.newLine();
							lexemeBuffer.clear();
							continue;
						}
						sourceChar.pollFirst();
					}
					break;
				case STATE_IDENTIFIER:
					if(Character.isLetter(c) || Character.isDigit(c)){
						putCharToLexemeBuffer(sourceChar.pollFirst());
						state = STATE_IDENTIFIER;
					}else{
						state = STATE_IDENTIFIER_END;
					}
					break;
				case STATE_IDENTIFIER_END:
					String identifier = getLexemeBuffer();
					if(IDType.isKeywords(identifier.trim())){
						Token token = new Token("KEYWORD", identifier, "_");
						outPutToken(token);
					}else{
						if (IDType.findID(identifier.trim()) == -1)
							IDType.insertID(identifier.trim());
						Token token = new Token("ID",identifier.trim(), Integer.toString(IDType.findID(identifier.trim())));
						outPutToken(token);
					}
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_INTEGER:
					if(Character.isDigit(c)){
						putCharToLexemeBuffer(sourceChar.pollFirst());
						state = STATE_INTEGER;
					}else{
						state = STATE_IDENTIFIER_END;
					}
					break;
				case STATE_INTEGER_END:
					String intStr = getFromLexemeBuffer();
					if (NumType.findNum(intStr.trim()) == -1)
						NumType.insertNum(intStr.trim());
					Token token = new Token( "Num", intStr.trim(),Integer.toString(NumType.findNum(intStr.trim())));	
					outPutToken(token);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_PLUS:
					Token plusToken = new Token("OPERATOR_PLUS", "+", "_");
					outPutToken(plusToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
				case STATE_MINUS:
					Token minusToken = new Token("OPERATOR_MINUS", "-", "_");
					outPutToken(minusToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
				case STATE_MULTIPLY:
					Token multiplyToken = new Token("OPERATOR_MULTIPLY", "*", "_");
					outPutToken(multiplyToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
				case STATE_DIVIDE:
					Token divideToken = new Token("OPERATOR_DIVIDE", "/", "_");
					outPutToken(divideToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
				case STATE_SEMICOLON:
					sourceChar.pollFirst();
					Token semicolonToken = new Token("SEPARATOR_SEMICOLON", ";", "_");
					outPutToken(semicolonToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
				case STATE_COMMA:
					Token commaToken = new Token("SEPARATOR_COMMA", ",", "_");
					outPutToken(commaToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
				case STATE_EQUAL_START:
					if(c == '='){
						sourceChar.pollFirst();
						state = STATE_EQUAL_END;
					}else{
						Token equalToken = new Token("OPERATOR_ASSIGN", "=", "_");
						outPutToken(equalToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;
				case STATE_EQUAL_END:
					Token equalEndToken = new Token("OPERATOR_EQUAL", "==", "_");
					outPutToken(equalEndToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_GREAT_START:
					if(c == '='){
						sourceChar.pollFirst();
						state = STATE_GREAT_EQUAL;
					}else{
						Token greatToken = new Token("OPERATOR_GREATER", ">", "_");
						outPutToken(greatToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;
				case STATE_GREAT_EQUAL:
					Token greatEqualToken = new Token("OPERATOR_GREATER_EQUAL", ">=", "_");
					outPutToken(greatEqualToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_LESS_START:
					if(c == '='){
						sourceChar.pollFirst();
						state = STATE_LESS_EQUAL;
					}else{
						Token lessToken = new Token("OPERATOR_LESS", "<", "_");
						outPutToken(lessToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;
				case STATE_LESS_EQUAL:
					Token lessEqualToken = new Token("OPERATOR_LESS_EQUAL", "<=", "_");
					outPutToken(lessEqualToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_NOT_EQUAL:
					if(c == '='){
						sourceChar.pollFirst();
						Token notEqualToken = new Token("OPERATOR_NOT_EQUAL", "!=", "_");
						outPutToken(notEqualToken);
						lexemeBuffer.clear();
						state = STATE_INITIAL;
					}
					break;
				case STATE_LEFT_PARENTHESIS:
					Token leftParenthesisToken = new Token("SEPARATOR_LEFT_PARENTHESIS", "(", "_");
					outPutToken(leftParenthesisToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_RIGHT_PARENTHESIS:
					Token rightParenthesisToken = new Token("SEPARATOR_RIGHT_PARENTHESIS", ")", "_");
					outPutToken(rightParenthesisToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_LEFT_BRACE:
					Token leftBraceToken = new Token("SEPARATOR_LEFT_BRACE", "{", "_");
					outPutToken(leftBraceToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_RIGHT_BRACE:
					Token rightBraceToken = new Token("SEPARATOR_RIGHT_BRACE", "}", "_");
					outPutToken(rightBraceToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_LEFT_BRACKET:
					Token leftBracketToken = new Token("SEPARATOR_LEFT_BRACKET", "[", "_");
					outPutToken(leftBracketToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;
				case STATE_RIGHT_BRACKET:
					Token rightBracketToken = new Token("SEPARATOR_RIGHT_BRACKET", "]", "_");
					outPutToken(rightBracketToken);
					lexemeBuffer.clear();
					state = STATE_INITIAL;
					break;				
			}
		}
	}
	

	public void analysis() throws IOException {	//分析操作
		int state = 0;
		Token token = new Token();
		while (!sourceChar.isEmpty()) {
			char c = readin();
			switch (state) {
			case 0:
				if (Character.isLetter(c))
					state = 1;
				else if (Character.isDigit(c))
					state = 3;
				else if (c == '+')
					state = 10;
				else if (c == '-')
					state = 14;
				else if (c == '*')
					state = 18;
				else if (c == '/')
					state = 21;
				else if (c == '%')
					state = 24;
				else if (c == '&')
					state = 27;
				else if (c == '|')
					state = 31;
				else if (c == '!')
					state = 35;
				else if (c == '^')
					state = 38;
				else if (c == '<')
					state = 41;
				else if (c == '>')
					state = 47;
				else if (c == '=')
					state = 57;
				else if (c == '?') {
					state = 60;
					readout();
				} else if (c == ':') {
					readout();
					state = 61;
				} else if (c == '[') {
					state = 62;
					readout();
				} else if (c == ']') {
					state = 63;
					readout();
				} else if (c == '(') {
					state = 64;
					readout();
				} else if (c == ')') {
					state = 65;
					readout();
				} else if (c == '.') {
					state = 66;
					readout();
				} else if (c == ',') {
					state = 67;
					readout();
				} else if (c == '{') {
					state = 68;
					readout();
				} else if (c == '}') {
					state = 69;
					readout();
				} else if (c == ';') {
					state = 70;
					readout();
				} else {
					if (c != ' ') {
						System.out.print("不能识别");
						output.write("不能识别");
						output.newLine();
						lexemeBuffer.clear();
						continue;
					}

				}
				break;
			case 1:
				if (Character.isLetter(c) || Character.isDigit(c))
					state = 1;
				else {
					readout();
					state = 2;
				}
				break;
			case 2: // 标识符或关键字类型
				readout();
				String string = getFromLexemeBuffer();
				if (IDType.isKeywords(string.trim())) { // 如果能在关键字表中找到，则是关键字类型
					token.setValue(string.trim());
					token.setType("keyword");
					token.setLocation("_");
				} else { // 否则，是标识符类型
					token.setValue(string.trim());
					if (IDType.findID(string.trim()) == -1)
						IDType.insertID(string.trim());
					token.setType("ID");
					int n = IDType.findID(string.trim());
					token.setLocation(Integer.toString(n));
				}
				token.display();
				output.write(token.toString());
				output.newLine();
				lexemeBuffer.clear();
				state = 0;
				break;
			case 3:
				if (Character.isDigit(c))
					state = 3;
				else if (c == '.')	//处理浮点数
					state = 4;
				else if (c == 'e' || c == 'E')	//处理科学计数法
					state = 6;	
				else {
					state = 9;	//否则读取数据完毕，转到状态9进行处理
					readout();
				}
				break;
			case 4:
				if (Character.isDigit(c))
					state = 5;
				else {
					if (c != ' '){
						System.out.print("不能识别");
						output.write("不能识别");
						output.newLine();
						lexemeBuffer.clear();
						continue;
					}
				}
				break;
			case 5:
				if (c == 'e' || c == 'E')
					state = 6;
				else {
					state = 9;
					readout();
				}
				break;
			case 6:
				if (c == '+' || c == '-')
					state = 7;
				else if (Character.isDigit(c))
					state = 8;
				else {
					if (c != ' '){
						System.out.print("不能识别");
						output.write("不能识别");
						output.newLine();
						lexemeBuffer.clear();
						continue;
					}
				}
				break;
			case 7:
				if (Character.isDigit(c))
					state = 8;
				else {
					System.out.print("不能识别");
					output.write("不能识别");
					output.newLine();
					lexemeBuffer.clear();
					continue;
				}
				break;
			case 8:
				if (Character.isDigit(c))
					state = 8;
				else {
					state = 9;
					readout();
				}
				break;
			case 9: // 数值类型，输出
				readout();
				String string1 = getFromLexemeBuffer();
				token.setValue(string1.trim());
				if (NumType.findNum(string1.trim()) == -1)
					NumType.insertNum(string1.trim());
				token.setType("Num");
				int n2 = NumType.findNum(string1.trim());
				token.setLocation(Integer.toString(n2));
				token.display();
				output.write(token.toString());
				output.newLine();
				lexemeBuffer.clear();
				state = 0;
				break;
			case 10:
				if (Character.isDigit(c))
					state = 3;
				else if (c == '+') {
					state = 11;
					readout();
				} else if (c == '=') {
					state = 12;
					readout();
				} else {
					state = 13;
					readout();
				}
				break;
			case 11: // ++
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 12: // +=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 13: // 只是单纯的+，则读出lexemeBuffer中的内容作为token，回到状态0
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 14:
				if (Character.isDigit(c))
					state = 3;
				else if (c == '-') {
					state = 15;
					readout();
				} else if (c == '=') {
					state = 16;
					readout();
				} else {
					state = 17;
					readout();
				}
				break;
			case 15: // --
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 16: // -=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 17: // -
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 18:
				if (c == '*') {
					state = 19;
					readout();
				} else {
					state = 20;
					readout();
				}
				break;
			case 19: // *=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 20: // *
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 21:
				if (c == '=') {
					state = 22;
					readout();
				} else {
					state = 23;
					readout();
				}
				break;
			case 22: // /=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 23: // /
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 24:
				if (c == '=') {
					state = 25;
					readout();
				} else {
					state = 26;
					readout();
				}
				break;
			case 25: // %=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 26: // %
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 27:
				if (c == '&') {
					state = 28;
					readout();
				} else if (c == '=') {
					state = 29;
					readout();
				} else {
					state = 30;
					readout();
				}
				break;
			case 28: // &&
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 29: // &=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 30: // &
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 31:
				if (c == '|') {
					state = 32;
					readout();
				} else if (c == '=') {
					state = 33;
					readout();
				} else {
					state = 34;
					readout();
				}
				break;
			case 32: // ||
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 33: // |=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 34: // |
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 35:
				if (c == '=') {
					state = 36;
					readout();
				} else {
					state = 37;
					readout();
				}
				break;
			case 36: // !=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 37: // !
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 38:
				if (c == '=') {
					state = 39;
					readout();
				} else {
					state = 40;
					readout();
				}
				break;
			case 39: // ^=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 40: // ^
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 41:
				if (c == '=') {
					state = 42;
					readout();
				} else if (c == '<') {
					state = 43;
				} else {
					state = 46;
					readout();
				}
				break;
			case 42: // <=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 43:
				if (c == '=') {
					state = 44;
					readout();
				} else {
					state = 45;
					readout();
				}
				break;
			case 44: // <<=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 45: // <<
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 46: // <
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 47:
				if (c == '=') {
					state = 48;
					readout();
				} else if (c == '>')
					state = 49;
				else {
					state = 55;
					readout();
				}
				break;
			case 48: // >=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 49:
				if (c == '>')
					state = 50;
				else if (c == '=') {
					state = 53;
					readout();
				} else {
					state = 54;
					readout();
				}
				break;
			case 50:
				if (c == '=') {
					state = 51;
					readout();
				} else {
					state = 52;
					readout();
				}
				break;
			case 51: // >>>=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 52: // >>>
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 53: // >>=
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 54: // >>
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 55: // >
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 56:
				break;
			case 57:
				if (c == '=') {
					state = 58;
					readout();
				} else {
					state = 59;
					readout();
				}
				break;
			case 58: // ==
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 59: // =
				readout();
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 60: // ?
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				output.write(token.toString());
				output.newLine();
				token.display();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 61: // :
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 62: // [
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 63: // ]
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 64: // (
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 65: // )
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 66: // .
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 67: // ,
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 68: // {
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 69: // }
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			case 70: // ;
				token.setValue(getFromLexemeBuffer().trim());
				token.setType("_");
				token.setLocation("_");
				token.display();
				output.write(token.toString());
				output.newLine();
				state = 0;
				lexemeBuffer.clear();
				break;
			}
		}
	}
	public char readin() {              //读入操作：取出sourceChar的第一个字符放入lexemeBuffer的末尾
		char a1 = sourceChar.pollFirst();
		lexemeBuffer.offerLast(a1);
		return a1;
	}

	public void readout() {            //读出操作；取出lexemeBuffer的最后一个字符放回sourceChar的开头
		char a2 = lexemeBuffer.pollLast();
		sourceChar.offerFirst(a2);
	}


	public String getFromLexemeBuffer() {       //获取lexemeBuffer里的内容以供输出
		String string = new String();
		for (int i = 0; i < lexemeBuffer.size(); i++) {
			char c = lexemeBuffer.get(i);
			string += c;
		}
		return string;
	}

	public static void main(String[] args) {
		Analyzer analyzer = new Analyzer();
		analyzer.getText();
	}
}
