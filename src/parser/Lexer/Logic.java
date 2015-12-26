package parser.Lexer;

public class Logic extends Token{
	public final String value;
	
	public Logic(String value, int operNum) {
		super(Tag.LOGIC);
		this.value = value;
		this.operNum = operNum;
	}
	public String toString() { 
		return new String("Logic: " + value);
	}
}
