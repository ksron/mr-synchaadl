package edu.postech.aadl.antlr.model;

import org.osate.ba.aadlba.ValueConstant;

public interface Constant extends CDExpression {

	public void setValue(ValueConstant val);

	public ValueConstant getValue();

	public String getText();
}
