package edu.postech.aadl.antlr.model.impl;

import org.osate.ba.aadlba.BehaviorVariableHolder;
import org.osate.ba.aadlba.DataSubcomponentHolder;
import org.osate.ba.aadlba.ValueVariable;

import edu.postech.aadl.antlr.model.Variable;

public class VariableImpl implements Variable {
	private ValueVariable vv = null;

	@Override
	public void setValue(ValueVariable val) {
		vv = val;
	}

	@Override
	public ValueVariable getValue() {
		return vv;
	}

	@Override
	public String getText() {
		if (vv instanceof DataSubcomponentHolder) {
			return ((DataSubcomponentHolder) vv).getDataSubcomponent().getName();
		} else if (vv instanceof BehaviorVariableHolder) {
			return ((BehaviorVariableHolder) vv).getBehaviorVariable().getName();
		}
		return "none";
	}
}
