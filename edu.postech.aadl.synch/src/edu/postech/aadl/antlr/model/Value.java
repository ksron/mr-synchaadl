package edu.postech.aadl.antlr.model;

public class Value implements ContDynamicsElem {
	private Variable var = null;
	private Constant con = null;
	private SimpleExpression expr = null;

	public Value(Variable var) {
		this.var = var;
	}

	public Value(Constant con) {
		this.con = con;
	}

	public Value(SimpleExpression expr) {
		this.expr = expr;
	}

}
