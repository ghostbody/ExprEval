package parser.Lexer;

public class Function extends Token{
	public final String value;
	public Function(String value) {
		super(Tag.FUNCTION);
		this.value = value;
	}
	public String toString() { 
		return "Function: " + value;
	}
}
