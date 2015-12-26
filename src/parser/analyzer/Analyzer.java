package parser.analyzer;

import java.util.ArrayList;
import java.util.Stack;

import parser.Lexer.Boolean;
import parser.Lexer.Bracket;
import parser.Lexer.Comma;
import parser.Lexer.Decimal;
import parser.Lexer.Dolla;
import parser.Lexer.Function;
import parser.Lexer.Lexer;
import parser.Lexer.Logic;
import parser.Lexer.Operator;
import parser.Lexer.Tag;
import parser.Lexer.Token;

public class Analyzer {
	private ArrayList<Token> tokens;
	private int currentToken = 0;
	private Stack<Token> operators;
	private Stack<Token> oprands;
	
	public Analyzer(String str) {
		Lexer lex = new Lexer(str);
		lex.scanner();
		this.tokens = lex.getAllTokens();
		operators = new Stack<Token>();
		operators.clear();
		oprands = new Stack<Token>();
		oprands.clear();
	}
	
	private Token getNextToken() {
		Token t = this.tokens.get(currentToken);
		currentToken++;
		return t;
	}
	
	private void shift(Token t) {
		operators.push(t);
	}
	
	private boolean operation(int op, Token t) throws Exception {
		boolean accept = false;
		switch (op) {
			case OOPTable.S0: shift(t); break;
			case OOPTable.R1: reduce(OOPTable.RBRACKET); break;
			case OOPTable.R2: reduce(OOPTable.RUNARY); break;
			case OOPTable.R3: reduce(OOPTable.RBOPER); break;
			case OOPTable.R4: reduce(OOPTable.RTOPER); break;
			case OOPTable.A0: accept = true; break;
			case OOPTable.E1: 
			case OOPTable.E2:
			case OOPTable.E3:
			case OOPTable.E4:
			case OOPTable.E7:
			case OOPTable.E8:
				throw new Exception();
			default:
				break;
		}
		return accept;
	}
	
	private void reduce(int reducer) {
		switch (reducer) {
			case OOPTable.RBRACKET: reduceBracket(); break;
			case OOPTable.RUNARY: reduceUnary(); break;
			case OOPTable.RBOPER: reduceBoper(); break;
			case OOPTable.RTOPER: reduceToper(); break;
			default: break;
		}
	}
	
	public Token parsing() throws Exception {
		Token t = new Dolla();
		operators.push(t);
		Token nextT = getNextToken();
		
		while(true) {
			t = operators.peek();
			if(OOPTable.getType(nextT) == OOPTable.bool || OOPTable.getType(nextT) == OOPTable.decimal) {
				oprands.push(nextT);
				nextT = getNextToken();
				continue;
			}
			int action = OOPTable.table[OOPTable.getType(t)][OOPTable.getType(nextT)];
			if(operation(action, nextT)) {
				break;
			}
			if(action == OOPTable.RBRACKET || action == OOPTable.SHIFT) {
				nextT = getNextToken();
			}
		}
		
		return oprands.peek();
	}

	private void reduceBracket() {
		Token t = operators.peek();
		boolean hasComma = false;
		while(true) {
			if(t instanceof Bracket && ((Bracket) t).value == '(') {
				operators.pop();
				break;
			} else {
				// if the operator is triple reduce RTOPER
				// else if operator is binary reduce RBOPER
				// else if operator is unary reduce RUOPER
				if(!hasComma && t.operNum == 3) {
					reduce(OOPTable.RTOPER);
				} else if(!hasComma && t.operNum == 2) {
					reduce(OOPTable.RBOPER);
				} else if(!hasComma && t.operNum == 1) {
					reduce(OOPTable.RUNARY);
				} else if(!hasComma && t instanceof Comma) {
					hasComma = true;
					operators.pop();
				}
			}
			t = operators.peek();
		}
		t = operators.peek();
		
		if(t instanceof Function) {
			if(((Function) t).value == "max" && hasComma) {
				Token temp1 = (Token)oprands.peek();
				oprands.pop();
				Token temp2 = (Token)oprands.peek();
				oprands.pop();
				if(temp1.tag == Tag.DECIMAL 
						&& temp2.tag == Tag.DECIMAL) {
					oprands.push(((Decimal)temp1).value > ((Decimal)temp2).value ? temp1 : temp2);
				}
			} else if (((Function) t).value == "min" && hasComma) {
				Token temp1 = (Token)oprands.peek();
				oprands.pop();
				Token temp2 = (Token)oprands.peek();
				oprands.pop();
				if(temp1.tag == Tag.DECIMAL 
						&& temp2.tag == Tag.DECIMAL) {
					oprands.push(((Decimal)temp1).value < ((Decimal)temp2).value ? temp1 : temp2);
				}
			} else if (((Function) t).value == "sin" && !hasComma) {
				Token temp = (Token)oprands.peek();
				oprands.pop();
				oprands.push(new Decimal(Math.sin(((Decimal)temp).value * Math.PI / 180)));
			} else if (((Function) t).value == "cos" && !hasComma) {
				Token temp = (Token)oprands.peek();
				oprands.pop();
				oprands.push(new Decimal(Math.cos(((Decimal)temp).value * Math.PI / 180 )));
			}
			operators.pop();
		}
		
	}
	
	private void reduceUnary() {
		Token operator = operators.peek();
		operators.pop();
		Token operand = oprands.peek();
		oprands.pop();
		if (operator instanceof Operator) {
			if(((Operator) operator).value == '-') {
				oprands.push(new Decimal(0 - ((Decimal)operand).value));
			}
		} else if (operand instanceof Logic) {
			if(((Operator)operator).value == '!') {
				oprands.push(new Boolean(!((Boolean)operand).value));
			}
		}
	}
	
	private void reduceBoper() {
		Token operator = operators.peek();
		operators.pop();
		Token operand1 = oprands.peek();
		oprands.pop();
		Token operand2 = oprands.peek();
		oprands.pop();
		
		if(operator instanceof Operator) {
			if(operand1 instanceof Decimal && operand2 instanceof Decimal) {
				switch (((Operator) operator).value) {
				case '+':
					oprands.push(new Decimal(((Decimal)operand2).value + ((Decimal)operand1).value)); return;
				case '-':
					oprands.push(new Decimal(((Decimal)operand2).value - ((Decimal)operand1).value)); return;
				case '*':
					oprands.push(new Decimal(((Decimal)operand2).value * ((Decimal)operand1).value)); return;
				case '/':
					oprands.push(new Decimal(((Decimal)operand2).value / ((Decimal)operand1).value)); return;
					// Notice Zero divide
				case '^':
					oprands.push(new Decimal(Math.pow(((Decimal)operand2).value, ((Decimal)operand1).value))); return;
				default:
					break;
				}
			}
		} else if (operator instanceof Logic) {
			double accuracy = 0.00000001;
			if(((Logic) operator).value == "&") {
				oprands.push(new Boolean(((Boolean)operand1).value & ((Boolean)operand2).value)); return;
			} else if(((Logic) operator).value == "|") {
				oprands.push(new Boolean(((Boolean)operand1).value | ((Boolean)operand2).value)); return;
			} else if(((Logic) operator).value == ">") {
				oprands.push(new Boolean(((Decimal)operand2).value > ((Decimal)operand1).value ? true : false)); return;
			} else if(((Logic) operator).value == ">=") {
				oprands.push(new Boolean(((Decimal)operand2).value > ((Decimal)operand1).value 
						|| Math.abs(((Decimal)operand2).value - ((Decimal)operand1).value) < accuracy ? true : false)); return;
			} else if(((Logic) operator).value == "<") {
				oprands.push(new Boolean(((Decimal)operand2).value < ((Decimal)operand1).value ? true : false)); return;
			} else if(((Logic) operator).value == "<=") {
				oprands.push(new Boolean(((Decimal)operand2).value < ((Decimal)operand1).value 
						|| Math.abs(((Decimal)operand2).value - ((Decimal)operand1).value) < accuracy ? true : false)); return;
			} else if(((Logic) operator).value == "=") {
				oprands.push(new Boolean(Math.abs(((Decimal)operand2).value - ((Decimal)operand1).value) < accuracy ? true : false)); return;
			} else if(((Logic) operator).value == "<>") {
				oprands.push(new Boolean(Math.abs(((Decimal)operand2).value - ((Decimal)operand1).value) > accuracy ? true : false)); return;
			}
		}
		
	}
	
	private void reduceToper() {
		Token operator1 = operators.peek();
		operators.pop();
		Token operator2 = operators.peek();
		operators.pop();
		Token operand1 = oprands.peek();
		oprands.pop();
		Token operand2 = oprands.peek();
		oprands.pop();
		Token operand3 = oprands.peek();
		oprands.pop();
		if(operator1 instanceof Logic && ((Logic)operator1).value == ":" &&
			operator2 instanceof Logic && ((Logic)operator2).value == "?") {
			if(operand1 instanceof Decimal && operand2 instanceof Decimal && operand3 instanceof Boolean) {
				if(((Boolean)operand3).value == true) {
					oprands.push(new Decimal(((Decimal)operand2).value));
				} else {
					oprands.push(new Decimal(((Decimal)operand1).value));
				}
			}
		}
	}
	
}
