package edu.postech.aadl.antlr.model;

public interface ContDynamicsItem extends ContDynamicsElem {
	public void setTarget(Target target);
	public void setExpression(CDExpression expr);

	public Target getTarget();

	public CDExpression getExpression();

}
