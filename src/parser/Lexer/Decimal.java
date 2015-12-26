package parser.Lexer;

public class Decimal extends Token{
	public final double value;
	public Decimal(double value) {
		super(Tag.DECIMAL);
		this.value = value;
	}
	public String toString() {
		return new String("Decimal: " + this.value);
	}
}
