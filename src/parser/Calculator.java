/**
 * @Copyright(C) 2008 Software Engineering Laboratory (SELAB), Department of Computer 
 * Science, SUN YAT-SEN UNIVERSITY. All rights reserved.
 **/

package parser;

import parser.Lexer.Boolean;
import parser.Lexer.Decimal;
import parser.Lexer.Token;
import parser.analyzer.Analyzer;
/**
 * Main program of the expression based calculator ExprEval
 * 
 * @author [PENDING your name]
 * @version 1.00 (Last update: [PENDING the last update])
 **/
public class Calculator {
	/**
	 * The main program of the parser. You should substitute the body of this
	 * method with your experiment result.
	 * 
	 * @param expression
	 *            user input to the calculator from GUI.
	 * @return if the expression is well-formed, return the evaluation result of
	 *         it.
	 * @throws Exception 
	 **/
	public double calculate(String expression) throws Exception {
		Analyzer an = new Analyzer(expression);
		Token resultToken = an.parsing();
		if(resultToken instanceof Boolean) {
			if(((Boolean) resultToken).value == true) {
				return 1;
			} else {
				return 0;
			}
		} else if(resultToken instanceof Decimal) {
			return ((Decimal) resultToken).value;
		}
		return 0;
	}
}
