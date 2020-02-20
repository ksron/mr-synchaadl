package edu.postech.aadl.synch.maude.parse.model;

import org.osate.aadl2.instance.ComponentInstance;
import org.osate.ba.aadlba.AadlBaFactory;
import org.osate.ba.aadlba.BehaviorPropertyConstant;
import org.osate.ba.aadlba.BehaviorRealLiteral;
import org.osate.ba.aadlba.ValueConstant;
import org.osate.ba.aadlba.impl.AadlBaFactoryImpl;
import org.osate.xtext.aadl2.properties.util.GetProperties;

public class Constant extends CDExpression {

	private String value;
	private ValueConstant typedValue;

	public Constant(String value, ComponentInstance ci) {
		this.value = value;
		this.typedValue = getTypedConstant(value, ci);
	}

	private ValueConstant getTypedConstant(String value, ComponentInstance ci) {
		AadlBaFactory aadlbaFactory = new AadlBaFactoryImpl();
		if (value.matches("([0-9]+)(\\.[0-9]+)?")) {
			BehaviorRealLiteral brl = aadlbaFactory.createBehaviorRealLiteral();
			brl.setValue(value);
			return brl;
		} else {
			BehaviorPropertyConstant cspr = aadlbaFactory.createBehaviorPropertyConstant();
			String[] prop = value.split("::");
			cspr.setProperty(GetProperties.lookupPropertyConstant(ci, prop[0], prop[1]));
			return cspr;
		}
	}

	public String getConstantString() {
		return value;
	}

	public ValueConstant getTypedConstant() {
		return typedValue;
	}

}
