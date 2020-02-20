package edu.postech.aadl.synch.maude.parse.model;

public class Variable extends Expression {

	public String variable;
	public String param;

	public Variable(String variable, String param) {
		this.variable = variable;
		this.param = param;
	}

	public Variable(String variable) {
		this.variable = variable;
		this.param = null;
	}
}
