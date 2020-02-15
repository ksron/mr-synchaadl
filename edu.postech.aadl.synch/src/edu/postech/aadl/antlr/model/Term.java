package edu.postech.aadl.antlr.model;

import java.util.ArrayList;
import java.util.List;


public class Term implements ContDynamicsElem {
	private List<Factor> factList;
	private List<FactorOperator> factOpList;

	public Term() {
		this.factList = new ArrayList<Factor>();
		this.factOpList = new ArrayList<FactorOperator>();
	}

	public void addFact(Factor fact) {
		factList.add(fact);
	}

	public void addFactOp(FactorOperator factOp) {
		factOpList.add(factOp);
	}
}
