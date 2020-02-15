package edu.postech.aadl.antlr.model.impl;

import java.util.ArrayList;
import java.util.List;

import edu.postech.aadl.antlr.model.Expression;
import edu.postech.aadl.antlr.model.FactorExpression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.ValueExpression;

public class FactorExpressionImpl implements FactorExpression {
	List<ValueExpression> exprList;
	List<Operator> opList;

	public FactorExpressionImpl() {
		exprList = new ArrayList<ValueExpression>();
		opList = new ArrayList<Operator>();
	}

	@Override
	public void addExpr(Expression expr) {
		exprList.add((ValueExpression) expr);
	}

	@Override
	public void addOp(Operator op) {
		opList.add(op);
	}

	public ValueExpression getFirstExpr() {
		return exprList.get(0);
	}

	public List<ValueExpression> getRemainExpr() {
		if (exprList.size() > 1) {
			return exprList.subList(1, exprList.size());
		}
		return exprList;
	}

	public List<Operator> getRemainOp() {
		return opList;
	}
}
