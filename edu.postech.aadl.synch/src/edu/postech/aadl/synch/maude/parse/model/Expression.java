package edu.postech.aadl.synch.maude.parse.model;

public class Expression {
	public final static String ADD = "+";
	public final static String MINUS = "-";
	public final static String MULTIPLY = "*";
	public final static String DIVIDE = "/";
	public final static String POWER = "^";

	protected String op;
	protected String unaryOp;
	protected Expression left;
	protected Expression right;

	public void setLeftExpression(Expression left) {
		this.left = left;
	}

	public void setRightExpression(Expression right) {
		this.right = right;
	}

	public void setOperation(String op) {
		switch (op) {
		case ADD:
			this.op = ADD;
			break;
		case MINUS:
			this.op = MINUS;
			break;
		case MULTIPLY:
			this.op = MULTIPLY;
			break;
		case DIVIDE:
			this.op = DIVIDE;
			break;
		case POWER:
			this.op = POWER;
			break;
		}
	}

	public void setUnaryOperation(String op) {
		switch (op) {
		case ADD:
			this.unaryOp = ADD;
			break;
		case MINUS:
			this.unaryOp = MINUS;
		}
	}
}
