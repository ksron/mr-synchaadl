package edu.postech.aadl.synch.maude.parse.model;

import org.eclipse.emf.common.util.ECollections;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.impl.Aadl2FactoryImpl;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.ba.aadlba.AadlBaFactory;
import org.osate.ba.aadlba.BehaviorVariable;
import org.osate.ba.aadlba.BehaviorVariableHolder;
import org.osate.ba.aadlba.DataSubcomponentHolder;
import org.osate.ba.aadlba.ValueVariable;
import org.osate.ba.aadlba.impl.AadlBaFactoryImpl;

public class Variable extends CDExpression {

	private String variable;
	private String param;
	private ValueVariable typedVariable;

	public Variable(String variable, String param, ComponentInstance ci) {
		this.variable = removeInitial(variable);
		this.param = param;
		this.typedVariable = getTypedVariable(ci, removeInitial(variable));
	}

	public Variable(String variable, ComponentInstance ci) {
		this.variable = removeInitial(variable);
		this.param = null;
		this.typedVariable = getTypedVariable(ci, removeInitial(variable));
	}

	public boolean hasParam() {
		if (param == null) {
			return false;
		}
		return true;
	}

	public String getVariableString() {
		return variable;
	}

	public String getParamString() {
		return param;
	}

	public ValueVariable getTypedVariable() {
		return typedVariable;
	}

	private String removeInitial(String variable) {
		if (variable.contains("(0)")) {
			return variable.substring(0, variable.indexOf("(0)"));
		}
		return variable;
	}

	private ValueVariable getTypedVariable(ComponentInstance ci, String variable) {
		AadlBaFactory aadlbaFactory = new AadlBaFactoryImpl();
		Aadl2Factory aadl2Factory = new Aadl2FactoryImpl();
		ValueVariable vv = null;
		if (checkHasDataSubCompHolder(ci, variable)) {
			vv = aadlbaFactory.createDataSubcomponentHolder();
			DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
			dsc.setName(variable);
			((DataSubcomponentHolder) vv).setDataSubcomponent(dsc);
		} else {
			vv = aadlbaFactory.createBehaviorVariableHolder();
			BehaviorVariable bv = aadlbaFactory.createBehaviorVariable();
			bv.setName(variable);
			((BehaviorVariableHolder) vv).setVariable(bv);
		}
		return vv;
	}

	private boolean checkHasDataSubCompHolder(ComponentInstance ci, String var) {
		ECollections.sort(ci.getComponentInstances(), (o1, o2) -> {
			if (o1.getName().toString().length() < o2.getName().toString().length()) {
				return 1;
			}
			return -1;
		});
		for (ComponentInstance o : ci.getComponentInstances()) {
			if (var.contains(o.getName())) {
				return true;
			}
		}
		return false;
	}
}
