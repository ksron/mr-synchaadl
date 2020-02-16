package edu.postech.aadl.antlr.model.impl;

import org.osate.ba.aadlba.BehaviorPropertyConstant;
import org.osate.ba.aadlba.BehaviorRealLiteral;
import org.osate.ba.aadlba.ValueConstant;

import edu.postech.aadl.antlr.model.Constant;

public class ConstantImpl implements Constant {

	private BehaviorRealLiteral brl;
	private BehaviorPropertyConstant bpc;

	@Override
	public void setValue(ValueConstant val) {
		if (val instanceof BehaviorRealLiteral) {
			brl = (BehaviorRealLiteral) val;
		} else if (val instanceof BehaviorPropertyConstant) {
			bpc = (BehaviorPropertyConstant) val;
		}
	}

	@Override
	public ValueConstant getValue() {
		if (brl != null) {
			return brl;
		} else if (bpc != null) {
			return bpc;
		}
		return null;
	}

	@Override
	public String getText() {
		if (brl != null) {
			return brl.toString();
		} else if (bpc != null) {
			return bpc.toString();
		}
		return null;
	}

}
