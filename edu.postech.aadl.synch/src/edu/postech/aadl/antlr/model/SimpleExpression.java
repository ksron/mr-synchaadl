package edu.postech.aadl.antlr.model;

import java.util.ArrayList;
import java.util.List;

public class SimpleExpression extends ContDynamicsItem {
	UnaryOperator unaryOp;
	List<TermOperator> termOpList;
	List<Term> termList;

	public SimpleExpression() {
		termOpList = new ArrayList<TermOperator>();
		termList = new ArrayList<Term>();
	}

	public void setUnaryOp(UnaryOperator unaryOp) {
		this.unaryOp = unaryOp;
	}

	public void addTermOp(TermOperator termOp) {
		termOpList.add(termOp);
	}

	public void addTerm(Term term) {
		termList.add(term);
	}

}
