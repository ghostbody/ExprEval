package parser.Lexer;

public class Tag {
    public static final int
    	// boolean false and true
	    BOOLEAN = 0,
	    // operators +,-,*,/,^,?,:
	    OPERATOR = 1,
	    // functions, sin, cos, max, min
	    FUNCTION = 2,
	    // bracket ( )
	    BRACKET = 3,
	    // logic operator >, >=, <, <=, =, <>, &, !
	    LOGIC = 4,
    	// Decimal
    	DECIMAL = 5,
    	// comma
    	COMMA = 6,
    	// dolla
    	DOLLA = 7;
}
