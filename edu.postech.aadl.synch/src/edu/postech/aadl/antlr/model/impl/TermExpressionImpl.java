package edu.postech.aadl.antlr.model.impl;

import java.util.ArrayList;
import java.util.List;

import edu.postech.aadl.antlr.model.Expression;
import edu.postech.aadl.antlr.model.FactorExpression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.TermExpression;


public class TermExpressionImpl implements TermExpression {

	List<FactorExpression> exprList;
	List<Operator> opList;

	public TermExpressionImpl() {
		exprList = new ArrayList<FactorExpression>();
		opList = new ArrayList<Operator>();
	}

	@Override
	public void addExpr(Expression expr) {
		exprList.add((FactorExpression) expr);
	}

	@Override
	public void addOp(Operator op) {
		opList.add(op);
	}
}
