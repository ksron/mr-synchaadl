package edu.postech.aadl.antlr.model;

import java.util.List;

public interface SimpleCDExpression extends BinaryCDExpression {

	public TermCDExpression getFirstExpression();
	public List<TermCDExpression> getNextExpressions();

	public TermCDExpression getNextExpression(int i);

}
