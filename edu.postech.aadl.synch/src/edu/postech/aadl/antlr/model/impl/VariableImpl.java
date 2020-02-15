package edu.postech.aadl.antlr.model.impl;

import edu.postech.aadl.antlr.model.Variable;

public class VariableImpl implements Variable {
	private String val;

	public VariableImpl(String val) {
		this.val = val;
	}
}
