package edu.postech.aadl.antlr.model.impl;

import java.util.ArrayList;
import java.util.List;

import edu.postech.aadl.antlr.model.CDExpression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.SimpleCDExpression;
import edu.postech.aadl.antlr.model.TermCDExpression;

public class TokenCDExpressionImpl implements SimpleCDExpression {
	private List<TermCDExpression> exprList;
	private List<Operator> opList;

	public TokenCDExpressionImpl() {
		exprList = new ArrayList<TermCDExpression>();
		opList = new ArrayList<Operator>();
	}

	@Override
	public void addExpr(CDExpression expr) {
		exprList.add((TermCDExpression) expr);
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
	public TermCDExpression getFirstExpression() {
		return exprList.get(0);
	}

	@Override
	public List<TermCDExpression> getNextExpressions() {
		if (exprList.size() > 1) {
			return exprList.subList(1, exprList.size());
		} else {
			return exprList;
		}
	}

	@Override
	public TermCDExpression getNextExpression(int i) {
		return getNextExpressions().get(i);
	}

	@Override
	public Operator getOp(int i) {
		return opList.get(i);
	}

}
