package parser.Lexer;


public abstract class Token {
	public final int tag;
	public int operNum;
	public Token(int tag) {
		this.tag = tag;
	}
	public String ToString() {
		return new String("tag: " + this.tag);
	}
}
