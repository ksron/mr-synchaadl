package edu.postech.aadl.antlr.model;

public interface Operator extends ContDynamicsElem {
	public static final String ADD = "+";
	public static final String MINUS = "-";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";
	public static final String POWER = "**";
	public void setValue(String val);

	public String getValue();
}
