package edu.postech.aadl.antlr.model;

public class Plus implements UnaryOperator, TermOperator {

	private String val = null;

	public Plus(String val) {
		this.val = val;
	}
}
