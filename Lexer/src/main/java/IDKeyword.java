package src.main.java;

public class IDKeyword {
	//c-语法关键字
	private final static String keywords[] = {"else","if", "int", "return", "void", "while"};

	public IDKeyword() {
		
	}

	public static boolean isKeywords(String s) {
		for (String string : keywords)
			if (s.equals(string))
				return true;
		return false;
	}


}
