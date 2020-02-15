package edu.postech.aadl.antlr.model;

public class Minus implements UnaryOperator, TermOperator {
	private String val = null;

	public Minus(String val) {
		this.val = val;
	}
}
