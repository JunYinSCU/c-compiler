
public class Token {

	private String value;
	private String type;
	private String location;

	@Override
	public String toString() {
		String string = "< " + this.value + " , " + this.type + " , "
				+ this.location + " >";
		return string;
	}

	public void display() {
		System.out.println("< " + this.value + " , " + this.type + " , "
				+ this.location + " >");
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Token(String type, String value,String location) {
		this.value = value;
		this.type = type;
		this.location = location;
		
	}

	public Token() {
		
	}

}
