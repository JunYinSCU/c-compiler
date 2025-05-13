package Lexical;

public class Token {

	private String value;	//token的值
	private String type;	//token的类型
	//token在源代码中的行号和列号
	private int row;
	private int column;

	@Override
	public String toString() {
		String string = "< " + this.type + " , " + this.value 
            + " , (row=" + this.row + " , column=" + this.column + ") >";
    	return string;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColum() { return column; }
	
	public void setColumn(int column) { this.column = column; }

	public Token(String type, String value,int row,int column) {
		this.value = value;
		this.type = type;
		this.row = row;
		this.column = column;
	}

	public Token() {
		
	}

	public Token(String type, String value) {
        this.value = value;
        this.type = type;
    }

}
