import static org.junit.Assert.*;
import org.junit.Test;
import exceptions.MissingLeftParenthesisException;
import parser.Calculator;


public class ExprEvalTest {
	
	@Test
	public void test() throws Exception {
		Calculator c = new Calculator();
		try {
			c.calculate("(2 + 3) ^ 3) - ((1 + 1)");
		} catch (Exception e) {
			assertTrue(e instanceof MissingLeftParenthesisException);
		}
	}

}
