package edu.postech.aadl.antlr.model.impl;

import edu.postech.aadl.antlr.model.Target;
import edu.postech.aadl.antlr.model.Variable;

public class TargetImpl implements Target {
	private Variable variable = null;
	private Variable param = null;

	public TargetImpl(Variable variable) {
		setVariable(variable);
	}

	@Override
	public void setVariable(Variable var) {
		variable = var;
	}

	@Override
	public void setParam(Variable prm) {
		param = prm;
	}

	@Override
	public Variable getVariable() {
		return variable;
	}

	@Override
	public Variable getParam() {
		return param;
	}
}
