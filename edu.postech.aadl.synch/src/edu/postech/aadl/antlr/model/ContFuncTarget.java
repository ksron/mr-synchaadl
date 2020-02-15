package edu.postech.aadl.antlr.model;

public class ContFuncTarget extends Target {
	private Variable var;
	private Variable param;

	public ContFuncTarget(Variable var, Variable param) {
		this.var = var;
		this.param = param;
	}
}
