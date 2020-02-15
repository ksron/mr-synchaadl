package edu.postech.aadl.antlr.model.impl;

import java.util.ArrayList;
import java.util.List;

import edu.postech.aadl.antlr.model.Expression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.TokenExpression;
import edu.postech.aadl.antlr.model.TermExpression;

public class TokenExpressionImpl implements TokenExpression {
	List<TermExpression> exprList;
	List<Operator> opList;

	public TokenExpressionImpl() {
		exprList = new ArrayList<TermExpression>();
		opList = new ArrayList<Operator>();
	}

	@Override
	public void addExpr(Expression expr) {
		exprList.add((TermExpression) expr);
	}

	@Override
	public void addOp(Operator op) {
		opList.add(op);
	}

}
