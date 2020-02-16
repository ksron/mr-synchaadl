package edu.postech.aadl.antlr.model.impl;

import edu.postech.aadl.antlr.model.CDExpression;
import edu.postech.aadl.antlr.model.Constant;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.SimpleCDExpression;
import edu.postech.aadl.antlr.model.ValueCDExpression;
import edu.postech.aadl.antlr.model.Variable;

public class ValueCDExpressionImpl implements ValueCDExpression {

	Variable variable = null;
	Constant constant = null;
	SimpleCDExpression expression = null;
	Operator unary;

	@Override
	public void addExpr(CDExpression expr) {
		if (expr instanceof Variable) {
			variable = (Variable) expr;
		} else if (expr instanceof Constant) {
			constant = (Constant) expr;
		} else if (expr instanceof SimpleCDExpression) {
			expression = (SimpleCDExpression) expr;
		}
	}

	@Override
	public void addOp(Operator op) {
		unary = op;
	}

	@Override
	public boolean hasMultiExpr() {
		return false;
	}

	@Override
	public int getExprCount() {
		return expression == null ? 0 : 1;
	}

	@Override
	public int getOpCount() {
		return unary == null ? 0 : 1;
	}

	@Override
	public Operator getOp(int i) {
		return unary;
	}

	@Override
	public CDExpression getFirstExpression() {
		if (variable != null) {
			return variable;
		} else if (constant != null) {
			return constant;
		} else if (expression != null) {
			return expression;
		}
		return null;
	}

}
