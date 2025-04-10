import java.util.Vector;

public class IDType {

	private static int numOfID = 0;
	private static Vector<String> iDs = new Vector<>();
	//c-语法关键字
	private final static String keywords[] = {"else","if", "int", "return", "void", "while"};


	public IDType() {
		
	}

	public static boolean isKeywords(String s) {
		for (String string : keywords)
			if (s.equals(string))
				return true;
		return false;
	}

	public static void insertID(String string) {
		iDs.addElement(string);
		numOfID++;
	}

	public static int findID(String s) {
		return iDs.indexOf(s);
	}

	public static int getNumOfID() {
		return numOfID;
	}

}
