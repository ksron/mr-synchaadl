package edu.postech.aadl.antlr.model.impl;

import edu.postech.aadl.antlr.model.Operator;

public class OperatorImpl implements Operator {
	private String val = null;

	public OperatorImpl(String val) {
		setValue(val);
	}

	@Override
	public void setValue(String val) {
		switch (val) {
		case Operator.ADD:
			this.val = Operator.ADD;
			break;
		case Operator.MINUS:
			this.val = Operator.MINUS;
			break;
		case Operator.MULTIPLY:
			this.val = Operator.MULTIPLY;
			break;
		case Operator.DIVIDE:
			this.val = Operator.DIVIDE;
			break;
		case Operator.POWER:
			this.val = Operator.POWER;
			break;
		default:
			System.out.println("Doesn't support unary operator: " + val);
		}
	}

	@Override
	public String getValue() {
		return val;
	}
}
