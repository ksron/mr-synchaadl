package edu.postech.aadl.antlr.model;

public class Factor implements ContDynamicsElem {

	private Value left;
	private Value right;
	private ValueOperator valOp;

	public void setLeftVal(Value val) {
		left = val;
	}

	public void setRightVal(Value val) {
		right = val;
	}

	public void setValOp(ValueOperator valOp) {
		this.valOp = valOp;
	}
}
