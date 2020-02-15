package edu.postech.aadl.antlr.model.impl;

import edu.postech.aadl.antlr.model.Expression;
import edu.postech.aadl.antlr.model.ODE;
import edu.postech.aadl.antlr.model.Target;

public class ODEImpl implements ODE {

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
