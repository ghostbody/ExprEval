package parser.Lexer;

public class Dolla extends Token{
	public Dolla() {
		super(Tag.DOLLA);
	}
	public String toString() { 
		return "$";
	}
}
