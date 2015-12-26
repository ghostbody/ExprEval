package parser.analyzer;

import java.util.ArrayList;
import java.util.Stack;

import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;

import exceptions.*;

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
	
	public Analyzer(String str) throws IllegalDecimalException, 
										LexicalException, 
										EmptyExpressionException {
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
	
	private boolean operation(int op, Token t) throws MissingLeftParenthesisException,
													MissingRightParenthesisException, 
													MissingOperandException, 
													TrinaryOperationException, 
													TypeMismatchedException, 
													FunctionCallException,
													SyntacticException,
													Exception,
													SemanticException{
		boolean accept = false;
		switch (op) {
			case OOPTable.S0: shift(t); break;
			case OOPTable.R1: reduce(OOPTable.RBRACKET); break;
			case OOPTable.R2: reduce(OOPTable.RUNARY); break;
			case OOPTable.R3: reduce(OOPTable.RBOPER); break;
			case OOPTable.R4: reduce(OOPTable.RTOPER); break;
			case OOPTable.A0: accept = true; break;
			case OOPTable.E1: throw new MissingLeftParenthesisException();
			case OOPTable.E2: throw new MissingRightParenthesisException();
			case OOPTable.E3: throw new MissingOperandException();
			case OOPTable.E4: throw new TrinaryOperationException();
			case OOPTable.E5: throw new TypeMismatchedException();
			case OOPTable.E6: throw new FunctionCallException();
			case OOPTable.E7: throw new SyntacticException();
			case OOPTable.E8: throw new Exception();
			default: throw new SemanticException();
		}
		return accept;
	}
	
	private void reduce(int reducer) throws DividedByZeroException, 
											MissingLeftParenthesisException, 
											MissingOperandException, 
											FunctionCallException, 
											TypeMismatchedException, 
											MissingOperatorException,
											TrinaryOperationException {
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
		
		if(oprands.size() > 1) {
			throw new MissingOperatorException();
		}
		
		return oprands.peek();
	}

	private void reduceBracket() throws DividedByZeroException, 
										MissingLeftParenthesisException, 
										MissingOperandException, 
										FunctionCallException, 
										TypeMismatchedException, 
										MissingOperatorException, 
										TrinaryOperationException {
		Token t = operators.peek();
		boolean hasComma = false;
		while(true) {
			if(t instanceof Dolla) {
				throw new MissingLeftParenthesisException();
			}
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
			if(((Function) t).value == "max") {
				if(oprands.size() < 2) {
					throw new MissingOperandException();
				}
				
				if(!hasComma) {
					throw new FunctionCallException();
				}
				Token temp1 = (Token)oprands.peek();
				oprands.pop();
				Token temp2 = (Token)oprands.peek();
				oprands.pop();
				if(temp1.tag == Tag.DECIMAL 
						&& temp2.tag == Tag.DECIMAL) {
					oprands.push(((Decimal)temp1).value > ((Decimal)temp2).value ? temp1 : temp2);
				}
			} else if (((Function) t).value == "min" && hasComma) {
				if(oprands.size() < 2) {
					throw new MissingOperandException();
				}
				
				if(!hasComma) {
					throw new FunctionCallException();
				}
				Token temp1 = (Token)oprands.peek();
				oprands.pop();
				Token temp2 = (Token)oprands.peek();
				oprands.pop();
				if(temp1.tag == Tag.DECIMAL 
						&& temp2.tag == Tag.DECIMAL) {
					oprands.push(((Decimal)temp1).value < ((Decimal)temp2).value ? temp1 : temp2);
				}
			} else if (((Function) t).value == "sin") {
				if(oprands.size() < 1) {
					throw new MissingOperandException();
				}
				
				if(hasComma) {
					throw new FunctionCallException();
				}
				
				Token temp = (Token)oprands.peek();
				oprands.pop();
				oprands.push(new Decimal(Math.sin(((Decimal)temp).value * Math.PI / 180)));
			} else if (((Function) t).value == "cos" && !hasComma) {
				if(oprands.size() < 1) {
					throw new MissingOperandException();
				}
				
				if(hasComma) {
					throw new FunctionCallException();
				}
				
				Token temp = (Token)oprands.peek();
				oprands.pop();
				oprands.push(new Decimal(Math.cos(((Decimal)temp).value * Math.PI / 180 )));
			}
			operators.pop();
		}
		
	}
	
	private void reduceUnary() throws MissingOperandException {
		if(oprands.empty()) {
			throw new MissingOperandException();
		}
		Token operator = operators.peek();
		operators.pop();
		Token operand = oprands.peek();
		oprands.pop();
		if (operator instanceof Operator) {
			if(((Operator) operator).value == '-') {
				oprands.push(new Decimal(0 - ((Decimal)operand).value));
			}
		} else if (operator instanceof Logic) {
			if(((Logic)operator).value == "!") {
				oprands.push(new Boolean(!((Boolean)operand).value));
			}
		}
	}
	
	private void reduceBoper() throws DividedByZeroException, 
										TypeMismatchedException, 
										MissingOperandException {
		if(oprands.size() < 2) {
			throw new MissingOperandException();
		}
		Token operator = operators.peek();
		operators.pop();
		Token operand1 = oprands.peek();
		oprands.pop();
		Token operand2 = oprands.peek();
		oprands.pop();
		
		if(operator instanceof Operator) {
				if(! ((operand2 instanceof Decimal) && (operand1 instanceof Decimal))) {
					throw new TypeMismatchedException();
				}
				switch (((Operator) operator).value) {
				case '+':
					oprands.push(new Decimal(((Decimal)operand2).value + ((Decimal)operand1).value)); return;
				case '-':
					oprands.push(new Decimal(((Decimal)operand2).value - ((Decimal)operand1).value)); return;
				case '*':
					oprands.push(new Decimal(((Decimal)operand2).value * ((Decimal)operand1).value)); return;
				case '/':
					if(((Decimal)operand1).value == 0) {
						throw new DividedByZeroException();
					}
					oprands.push(new Decimal(((Decimal)operand2).value / ((Decimal)operand1).value)); return;
				case '^':
					oprands.push(new Decimal(Math.pow(((Decimal)operand2).value, ((Decimal)operand1).value))); return;
				default:
					break;
				}
		} else if((operator instanceof Logic)) {
			double accuracy = 0.00000001;
			if(((Logic) operator).value == "&") {
				oprands.push(new Boolean(((Boolean)operand1).value && ((Boolean)operand2).value)); return;
			} else if(((Logic) operator).value == "|") {
				oprands.push(new Boolean(((Boolean)operand1).value || ((Boolean)operand2).value)); return;
			}
			
			if (! ((operand2 instanceof Decimal) && (operand1 instanceof Decimal))) {
				throw new TypeMismatchedException();
			}
			
			if(((Logic) operator).value == ">") {
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
		} else {
			throw new MissingOperandException();
		}
		
	}
	
	private void reduceToper() throws MissingOperandException, MissingOperatorException, TrinaryOperationException {
		if(operators.size() < 2) {
			throw new MissingOperatorException();
		}
		
		if(oprands.size() < 3) {
			throw new MissingOperandException();
		}
		
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
		if(operator1 instanceof Operator && ((Operator)operator1).value == ':' &&
			operator2 instanceof Operator && ((Operator)operator2).value == '?') {
			if(operand1 instanceof Decimal && operand2 instanceof Decimal && operand3 instanceof Boolean) {
				if(((Boolean)operand3).value == true) {
					oprands.push(new Decimal(((Decimal)operand2).value));
				} else {
					oprands.push(new Decimal(((Decimal)operand1).value));
				}
			} else {
				throw new TrinaryOperationException();
			}
		} else {
			throw new TrinaryOperationException();
		}
	}
	
}
