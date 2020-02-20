package edu.postech.aadl.synch.maude.parse.model;

public class CDExpression {
	public final static String ADD = "+";
	public final static String MINUS = "-";
	public final static String MULTIPLY = "*";
	public final static String DIVIDE = "/";
	public final static String POWER = "^";

	protected String op = null;
	protected String unaryOp = null;
	protected CDExpression left = null;
	protected CDExpression right = null;

	public void setLeftExpression(CDExpression left) {
		this.left = left;
	}

	public void setRightExpression(CDExpression right) {
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

	public String getOp() {
		return op;
	}

	public String getUnaryOp() {
		return unaryOp;
	}

	public CDExpression getLeft() {
		return left;
	}

	public CDExpression getRight() {
		return right;
	}

}
