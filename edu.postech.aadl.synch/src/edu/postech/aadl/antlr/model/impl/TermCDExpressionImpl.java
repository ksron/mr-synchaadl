package edu.postech.aadl.antlr.model.impl;

import java.util.ArrayList;
import java.util.List;

import edu.postech.aadl.antlr.model.CDExpression;
import edu.postech.aadl.antlr.model.FactorCDExpression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.TermCDExpression;


public class TermCDExpressionImpl implements TermCDExpression {

	private List<FactorCDExpression> exprList;
	private List<Operator> opList;

	public TermCDExpressionImpl() {
		exprList = new ArrayList<FactorCDExpression>();
		opList = new ArrayList<Operator>();
	}

	@Override
	public void addExpr(CDExpression expr) {
		exprList.add((FactorCDExpression) expr);
	}

	@Override
	public void addOp(Operator op) {
		opList.add(op);
	}

	@Override
	public boolean hasMultiExpr() {
		return !opList.isEmpty() && exprList.size() > 1;
	}

	@Override
	public int getExprCount() {
		return exprList.size();
	}

	@Override
	public int getOpCount() {
		return opList.size();
	}

	@Override
	public FactorCDExpression getFirstExpression() {
		return exprList.get(0);
	}

	@Override
	public List<FactorCDExpression> getNextExpressions() {
		if (exprList.size() > 1) {
			return exprList.subList(1, exprList.size());
		} else {
			return exprList;
		}
	}

	@Override
	public FactorCDExpression getNextExpression(int i) {
		return getNextExpressions().get(i);
	}


	@Override
	public Operator getOp(int i) {
		return opList.get(i);
	}
}
