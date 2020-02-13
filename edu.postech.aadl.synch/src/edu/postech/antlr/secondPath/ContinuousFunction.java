package edu.postech.antlr.secondPath;

import java.util.ArrayList;
import java.util.List;

import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.impl.Aadl2FactoryImpl;
import org.osate.ba.aadlba.AadlBaFactory;
import org.osate.ba.aadlba.AssignmentAction;
import org.osate.ba.aadlba.BehaviorAction;
import org.osate.ba.aadlba.BehaviorActionSequence;
import org.osate.ba.aadlba.BehaviorVariable;
import org.osate.ba.aadlba.BehaviorVariableHolder;
import org.osate.ba.aadlba.DataSubcomponentHolder;
import org.osate.ba.aadlba.Target;
import org.osate.ba.aadlba.ValueExpression;
import org.osate.ba.aadlba.ValueVariable;
import org.osate.ba.aadlba.impl.AadlBaFactoryImpl;

import edu.postech.aadl.synch.maude.template.RtmAadlBehaviorLanguage;
import edu.postech.aadl.synch.maude.template.RtmAadlModel;

public class ContinuousFunction {

	private RtmAadlBehaviorLanguage bl;

	private BehaviorActionSequence bas;
	private List<DataSubcomponentHolder> funcId;
	private List<BehaviorVariableHolder> varId;
	private List<ValueExpression> ve;
	private int count = 0;

	private AadlBaFactory aadlbaFactory;
	private Aadl2Factory aadl2Factory;

	public ContinuousFunction(BehaviorActionSequence bas, RtmAadlBehaviorLanguage bl) {
		this.bas = bas;
		this.bl = bl;
		funcId = new ArrayList<DataSubcomponentHolder>();
		varId = new ArrayList<BehaviorVariableHolder>();
		ve = new ArrayList<ValueExpression>();
		aadlbaFactory = new AadlBaFactoryImpl();
		aadl2Factory = new Aadl2FactoryImpl();
	}

	public void parse() {
		for (BehaviorAction ba : bas.getActions()) {
			ve.add(((AssignmentAction) ba).getValueExpression());
			putFuncIdAndVarId(((AssignmentAction) ba).getTarget());
			count++;
		}
	}

	private void putFuncIdAndVarId(Target target) {
		// FlowsVisitor insert the data like "x(t)" into DataSubcomponentHolder.Name
		// You should divide it into two component, DataSubcomponentHolder : x and BehaviorVariableHolder : t
		String targetName = ((DataSubcomponentHolder) target).getDataSubcomponent().getName();
		String first = targetName.substring(0, targetName.indexOf('('));
		String second = targetName.substring(targetName.indexOf('(') + 1, targetName.indexOf(')'));

		DataSubcomponentHolder dscHolder = aadlbaFactory.createDataSubcomponentHolder();
		DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
		dsc.setName(first);
		dscHolder.setDataSubcomponent(dsc);
		funcId.add(dscHolder);

		ValueVariable vv = aadlbaFactory.createBehaviorVariableHolder();
		BehaviorVariable bv = aadlbaFactory.createBehaviorVariable();
		bv.setName(second);
		((BehaviorVariableHolder) vv).setVariable(bv);
		varId.add((BehaviorVariableHolder) vv);
	}

	public void setVarId(RtmAadlModel ram) {
		for (BehaviorVariableHolder bvh : varId) {
			ram.id(bvh.getBehaviorVariable(), "VarId");
		}
	}

	public String getMaude() {
		String maude = "";
		for (int i = 0; i < count; i++) {
			maude += "(" + getMaude(i) + ") ; ";
		}
		return maude.substring(0, maude.length() - 2);
	}

	private String getMaude(int i) {
		return funcId.get(i).getDataSubcomponent().getName() + "(" + varId.get(i).getBehaviorVariable().getName() + ")"
				+ " = " + bl.compileFlowsExpression(ve.get(i));

	}
}
