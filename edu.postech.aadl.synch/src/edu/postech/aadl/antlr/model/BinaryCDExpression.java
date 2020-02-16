package edu.postech.aadl.antlr.model;

public interface BinaryCDExpression extends CDExpression {
	public void addExpr(CDExpression expr);
	public void addOp(Operator op);
	public boolean hasMultiExpr();

	public int getExprCount();

	public int getOpCount();

	public Operator getOp(int i);
}
