package edu.postech.aadl.antlr.model;

public interface BinaryExpression extends Expression {
	public void addExpr(Expression expr);
	public void addOp(Operator op);
}
