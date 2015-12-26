package parser.Lexer;

public class Comma extends Token {
	public final char value = ',';
	public Comma() {
		super(Tag.COMMA);
	}
	public String toString() { 
		return "Comma: " + value;
	}
}
