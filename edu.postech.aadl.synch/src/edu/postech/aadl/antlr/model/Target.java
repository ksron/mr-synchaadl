package edu.postech.aadl.antlr.model;

public interface Target extends ContDynamicsElem {
	public void setVariable(Variable var);

	public void setParam(Variable var);

	public Variable getVariable();

	public Variable getParam();

}
