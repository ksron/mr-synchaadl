package edu.postech.aadl.antlr.model;

import java.util.List;

public interface FactorCDExpression extends BinaryCDExpression {
	public ValueCDExpression getFirstExpression();
	public List<ValueCDExpression> getNextExpressions();

	public ValueCDExpression getNextExpression(int i);
}
