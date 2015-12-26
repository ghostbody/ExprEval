package parser.Lexer;

public class Operator extends Token{
	public final char value;
	public Operator(char value, int operNum) {
		super(Tag.OPERATOR);
		this.value = value;
		this.operNum = operNum;
	}
	public String toString() { 
		return "Operator: " + value + " " + operNum;
	}
}
