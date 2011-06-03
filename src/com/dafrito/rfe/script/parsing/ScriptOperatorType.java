/**
 * 
 */
package com.dafrito.rfe.script.parsing;

public enum ScriptOperatorType {
	EQUIVALENCY("==", "Equivalency"),
	NONEQUIVALENCY("!=", "Nonequivalency"),
	LESS("<", "Less than"),
	LESSEQUALS("<=", "Less than or equals to"),
	GREATEREQUALS(">=", "Greater than or equals to"),
	GREATER(">", "Greater than"),
	NOT("!", "Not"),
	AND("&&", "And"),
	OR("||", "Or"),
	ASSIGNMENT("=", "Assign"),
	MINUS("-", "Minus"),
	PLUS("+", "Add"),
	MULTIPLY("*", "Multiply"),
	DIVIDE("/", "Divide"),
	MODULUS("%", "Modulus"),
	INCREMENT("++", "Increment"),
	DECREMENT("--", "Decrement"),
	PLUSEQUALS("+=", "Plus-equals"),
	MINUSEQUALS("-=", "Minus-equals"),
	MULTIPLYEQUALS("*=", "Multiply-equals"),
	DIVIDEEQUALS("/=", "Divide-equals"),
	MODULUSEQUALS("%=", "Modulus-equals"),
	PERIOD(".", "Member"),
	COLON(":", "Colon"),
	COMMA(",", "Comma"),
	POUNDSIGN("#", "Pound"),
	SEMICOLON(";", "Semicolon");

	private final String name;
	private final String operator;

	private ScriptOperatorType(String operator, String name) {
		this.operator = operator;
		this.name = name;
	}

	public String getOperator() {
		return this.operator;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static ScriptOperatorType parse(String operatorString) {
		if (operatorString == null) {
			throw new NullPointerException("operatorString must not be null");
		}
		for (ScriptOperatorType op : ScriptOperatorType.values()) {
			if (op.getOperator().equals(operatorString)) {
				return op;
			}
		}
		return null;
	}

}