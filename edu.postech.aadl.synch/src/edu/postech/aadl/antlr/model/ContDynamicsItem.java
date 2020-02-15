package edu.postech.aadl.antlr.model;

public class ContDynamicsItem implements ContDynamicsElem {

	private Target target;
	private SimpleExpression expr;

	public void setTarget(Target target) {
		this.target = target;
	}

	public void setExpr(SimpleExpression expr) {
		this.expr = expr;
	}
}
