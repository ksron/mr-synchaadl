package edu.postech.aadl.utils;

import java.util.ArrayList;
import java.util.List;

import org.osate.ba.aadlba.ValueVariable;

import edu.postech.aadl.synch.maude.parse.model.ContDynamicsItem;
import edu.postech.aadl.synch.maude.parse.model.FactorCDExpression;
import edu.postech.aadl.synch.maude.parse.model.SimpleCDExpression;
import edu.postech.aadl.synch.maude.parse.model.TermCDExpression;
import edu.postech.aadl.synch.maude.parse.model.Variable;

public class ParseUtil {

	public static String getParamString(final ContDynamicsItem item) {
		if (item.getTarget().hasParam()) {
			return item.getTarget().getParamString();
		}
		return null;
	}

	public static List<ValueVariable> getTypedVariableList(final ContDynamicsItem item) {
		List<ValueVariable> vvList = new ArrayList<ValueVariable>();
		getTypedVariableList((SimpleCDExpression) item.getExpression(), vvList);
		return vvList;
	}

	private static void getTypedVariableList(SimpleCDExpression expr, List<ValueVariable> vvList) {
		getTypedVariableList((TermCDExpression) expr.getLeft(), vvList);
		if (expr.getOp() != null) {
			getTypedVariableList((SimpleCDExpression) expr.getRight(), vvList);
		}
	}

	private static void getTypedVariableList(TermCDExpression expr, List<ValueVariable> vvList) {
		getTypedVariableList((FactorCDExpression) expr.getLeft(), vvList);
		if (expr.getOp() != null) {
			getTypedVariableList((TermCDExpression) expr.getRight(), vvList);
		}
	}

	private static void getTypedVariableList(FactorCDExpression expr, List<ValueVariable> vvList) {
		if(expr.getLeft() instanceof Variable) {
			vvList.add(((Variable) expr.getLeft()).getTypedVariable());
		} else if (expr.getLeft() instanceof SimpleCDExpression) {
			getTypedVariableList((SimpleCDExpression) expr.getLeft(), vvList);
		}
		if (expr.getOp() != null) {
			getTypedVariableList((FactorCDExpression) expr.getRight(), vvList);
		}
	}
}
