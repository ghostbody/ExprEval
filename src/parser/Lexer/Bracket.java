package parser.Lexer;

public class Bracket extends Token{
	public final char value;
	public Bracket(char value) {
		super(Tag.BRACKET);
		this.value = value;
	}
	public String toString() { 
		return "Bracket: " + value;
	}
}
