package edu.postech.aadl.antlr.model;

import org.osate.ba.aadlba.ValueVariable;

public interface Variable extends CDExpression {
	public void setValue(ValueVariable val);

	public ValueVariable getValue();

	public String getText();
}
