package parser.Lexer;

import java.math.BigDecimal;
import java.util.ArrayList;


public class Lexer {
	private String inputString;
	private int lookahead = 0;
	private ArrayList<Token> tokenList;
	
	public Lexer(String inputString) {
		this.inputString = inputString;
		this.tokenList = new ArrayList<Token>();
		preProcess();
	}
	
	private void preProcess() {
		this.inputString = this.inputString.replace(" ", "");
		this.inputString += "#";
		System.out.println(inputString);
	}
	
	public ArrayList<Token> getAllTokens() {
		return this.tokenList;
	}
	
	public void scanner() {	
		while(lookahead < inputString.length() -1) {
			this.getNextToken();
		}
		this.tokenList.add(new Dolla());
	}
	
	public Token getNextToken() {
		Token t = scan();
		this.tokenList.add(t);
		return t;
	}
	
	private Token scan() {
		char temp = inputString.charAt(lookahead);
		if(match(temp, Tag.BOOLEAN)) {
			if(matchBool_true()) {
				return new Boolean(true);
			}
			
			if(matchBool_false()) {
				return new Boolean(false);
			}
		} else if (match(temp, Tag.OPERATOR)) {
			lookahead++;
			switch (temp) {
				case '+': return new Operator('+', 2);
				case '-': 
					if(lookahead == 1) {
						return new Operator('-', 1);
					} else if(tokenList.get(tokenList.size()-1) instanceof Operator && 
							isOperator(((Operator)tokenList.get(tokenList.size()-1)).value)) {
						return new Operator('-', 1);
					} else if(tokenList.get(tokenList.size()-1) instanceof Bracket && 
							((Bracket)tokenList.get(tokenList.size()-1)).value == '('){
						return new Operator('-', 1);
					} else{
						return new Operator('-', 2);
					}
				case '*': return new Operator('*', 2);
				case '/': return new Operator('/', 2);
				case '^': return new Operator('^', 2);
				case '?': return new Operator('?', 3);
				case ':': return new Operator(':', 3);
			}
		} else if (match(temp, Tag.FUNCTION)) {
			if(matchFunction_cos()) {
				return new Function("cos");
			}
			if(matchFunction_sin()) {
				return new Function("sin");
			}
			if(matchFunction_max()) {
				return new Function("max");
			}
			lookahead--;
			if(matchFunction_min()) {
				return new Function("min");
			}
		} else if (match(temp, Tag.BRACKET)) {
			lookahead++;
			switch (temp) {
				case '(': return new Bracket('(');
				case ')': return new Bracket(')');
			}
		} else if (match(temp, Tag.LOGIC)) {
			lookahead++;
			switch (temp) {
				case '<':
					if(inputString.charAt(lookahead) == '=') {
						lookahead++;
						return new Logic("<=",2);
					} else if (inputString.charAt(lookahead) == '>') {
						lookahead++;
						return new Logic("<>",2);
					}
					return new Logic("<", 2);
				case '>':
					if(inputString.charAt(lookahead) == '=') {
						lookahead++;
						return new Logic(">=", 2);
					}
					return new Logic(">", 2);
				case '=':
					return new Logic("=", 2);
				case '&':
					return new Logic("&", 2);
				case '!':
					return new Logic("!", 1);
			}
		} else if (match(temp, Tag.DECIMAL)) {
			int posStart = lookahead;
			if(matchDecimal_decimal()) {
				String num = inputString.substring(posStart, lookahead);
				return new Decimal(new BigDecimal(num).doubleValue());
			}
		} else if (match(temp, Tag.COMMA)) {
			return new Comma();
		}
		
		return null;
	}
	
	private boolean isOperator(char sign) {
		if(sign == '+' || sign == '-' || sign == '*' || sign == '/' || sign == '^' || sign == '(') {
			return true;
		}
		return false;
	}

	private boolean match(char element, int tag) {
		if(element >= '0' && element <= '9') {
			return tag == Tag.DECIMAL;
		} else if (element == 't' || element == 'f') {
			return tag == Tag.BOOLEAN;
		} else if (element == '+' || element == '-' || element == '*' 
				|| element == '/' || element == '^' || element == '?'
				|| element == ':') {
			return tag == Tag.OPERATOR;
		} else if (element == 's' || element == 'c' || element == 'm') {
			return tag == Tag.FUNCTION;
		} else if (element == '(' || element == ')') {
			return tag == Tag.BRACKET;
		} else if (element == '>' || element == '<' || element == '=' 
				|| element == '&' || element == '!') {
			return tag == Tag.LOGIC;
		} else if (element == ',') {
			return tag == Tag.COMMA;
		}
		return false;
	}
	
	private boolean matchDecimal_integral() {
		boolean flag = false;
		while(inputString.charAt(lookahead) >= '0' 
				&& inputString.charAt(lookahead) <= '9') {
			lookahead++;
			flag = true;
		}
		lookahead--;
		return flag;
	}
	
	private boolean matchDecimal_fraction() {
		if(inputString.charAt(lookahead) == '.') {
			lookahead++;
			return matchDecimal_integral();
		}
		return false;
	}
	
	static final int[][] exponentDFA = {
		{ 1,  2, -1, -1, -1 },  // State A
		{-1, -1,  3,  4,  5 },
		{-1, -1,  3,  4,  5 },
		{-1, -1, -1, -1,  5 },
		{-1, -1, -1, -1,  5 },
		{-1, -1, -1, -1, -1 }
	};
	
	static final char[] exponentSign = {
		'E', 'e', '+', '-', 'i'
	};
	
	static final int[] exponentAccept = {5};
	
	private boolean matchDecimal_exponent() {
		int currentState = 0;
		boolean flag;
		while(true) {
			flag = false;
			for(int i = 0; i < 5; i++) {
				if(exponentDFA[currentState][i] != -1) {
					if((i != 4 && exponentSign[i] == inputString.charAt(lookahead))
							|| (i == 4 && matchDecimal_integral())) {
						currentState = exponentDFA[currentState][i];
						flag = true;
						break;
					}
				}
			}

			if(flag == false) {
				for (int i : exponentAccept) {
					if(currentState == i) {
						lookahead--;
						return true;
					}
				}
				return false;
			} else {
				lookahead++;
			}
		}
	}
	
	private boolean matchDecimal_decimal() {
		if(matchDecimal_integral()) {
			lookahead++;
			if(matchDecimal_fraction()) {
				lookahead++;
				if(matchDecimal_exponent()) {
					lookahead++;
					return true;
				}
				return true;
			}
			if(matchDecimal_exponent()) {
				lookahead++;
				return true;
			}
			return true;
		}
		return false;
	}
	
	private boolean matchFunction_sin() {
		if(inputString.charAt(lookahead) == 's') {
			lookahead++;
			if(inputString.charAt(lookahead) == 'i') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 'n') lookahead++; else return false;
			return true;
		}
		return false;
	}
	
	private boolean matchFunction_cos() {
		if(inputString.charAt(lookahead) == 'c') {
			lookahead++;
			if(inputString.charAt(lookahead) == 'o') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 's') lookahead++; else return false;
			return true;
		}
		return false;
	}
	
	private boolean matchFunction_max() {
		if(inputString.charAt(lookahead) == 'm') {
			lookahead++;
			if(inputString.charAt(lookahead) == 'a') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 'x') lookahead++; else return false;
			return true;
		}
		return false;
	}
	
	private boolean matchFunction_min() {
		if(inputString.charAt(lookahead) == 'm') {
			lookahead++;
			if(inputString.charAt(lookahead) == 'i') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 'n') lookahead++; else return false;
			return true;
		}
		return false;
	}
	
	private boolean matchBool_true() {
		if(inputString.charAt(lookahead) == 't') {
			lookahead++;
			if(inputString.charAt(lookahead) == 'r') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 'u') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 'e') lookahead++; else return false;
			return true;
		}
		return false;
	}
	
	private boolean matchBool_false() {
		if(inputString.charAt(lookahead) == 'f') {
			lookahead++;
			if(inputString.charAt(lookahead) == 'a') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 'l') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 's') lookahead++; else return false;
			if(inputString.charAt(lookahead) == 'e') lookahead++; else return false;
			return true;
		}
		return false;
	}

}
