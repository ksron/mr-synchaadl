package edu.postech.aadl.antlr.model.impl;

import edu.postech.aadl.antlr.model.ContFunc;
import edu.postech.aadl.antlr.model.Expression;
import edu.postech.aadl.antlr.model.Target;

public class ContFuncImpl implements ContFunc {
	public Target target = null;
	public Expression expression = null;

	@Override
	public void setTarget(Target targ) {
		target = targ;
	}

	@Override
	public void setExpression(Expression expr) {
		expression = expr;
	}

	@Override
	public Target getTarget() {
		return target;
	}

	@Override
	public Expression getExpression() {
		return expression;
	}
}
