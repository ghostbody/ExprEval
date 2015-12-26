package parser.Lexer;

public class Boolean extends Token{
	public final boolean value;
	public Boolean(boolean value) {
		super(Tag.BOOLEAN);
		this.value = value;
	}
	public String toString() { 
		return "Boolean: " + value;
	}
}
