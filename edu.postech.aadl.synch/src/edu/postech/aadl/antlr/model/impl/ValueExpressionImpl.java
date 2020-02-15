package edu.postech.aadl.antlr.model.impl;

import java.util.ArrayList;
import java.util.List;

import edu.postech.aadl.antlr.model.Expression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.ValueExpression;

public class ValueExpressionImpl implements ValueExpression {

	List<Expression> exprList;
	List<Operator> opList;

	public ValueExpressionImpl() {
		exprList = new ArrayList<Expression>();
		opList = new ArrayList<Operator>();
	}

	@Override
	public void addExpr(Expression expr) {
		exprList.add(expr);

	}

	@Override
	public void addOp(Operator op) {
		opList.add(op);
	}

}
