package edu.postech.aadl.antlr.model;

import java.util.List;

public interface TermCDExpression extends BinaryCDExpression {
	public FactorCDExpression getFirstExpression();

	public List<FactorCDExpression> getNextExpressions();

	public FactorCDExpression getNextExpression(int i);
}
