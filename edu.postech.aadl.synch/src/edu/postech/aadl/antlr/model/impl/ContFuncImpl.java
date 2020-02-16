package edu.postech.aadl.antlr.model.impl;

import edu.postech.aadl.antlr.model.CDExpression;
import edu.postech.aadl.antlr.model.ContFunc;
import edu.postech.aadl.antlr.model.Target;

public class ContFuncImpl implements ContFunc {
	public Target target = null;
	public CDExpression expression = null;

	@Override
	public void setTarget(Target targ) {
		target = targ;
	}

	@Override
	public void setExpression(CDExpression expr) {
		expression = expr;
	}

	@Override
	public Target getTarget() {
		return target;
	}

	@Override
	public CDExpression getExpression() {
		return expression;
	}
}
