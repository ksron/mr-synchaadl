package edu.postech.aadl.synch.checker;

import java.util.ArrayList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.DirectedFeature;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.FeatureCategory;
import org.osate.aadl2.instance.FeatureInstance;
import org.osate.aadl2.instance.InstanceObject;
import org.osate.aadl2.instance.util.InstanceSwitch;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.util.Aadl2InstanceUtil;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import edu.postech.aadl.antlr.ContDynamicsFlowsVisitor;
import edu.postech.aadl.antlr.ContDynamicsLexer;
import edu.postech.aadl.antlr.ContDynamicsParser;
import edu.postech.aadl.antlr.model.ContDynamics;
import edu.postech.aadl.utils.PropertyUtil;

public class SynchAadlConstChecker extends AadlProcessingSwitchWithProgress {

	public SynchAadlConstChecker(IProgressMonitor pm, AnalysisErrorReporterManager errMgr) {
		super(pm, errMgr);
	}

	@Override
	protected void initSwitches() {
		instanceSwitch = new InstanceSwitch<String>() {

			@Override
			public String caseComponentInstance(ComponentInstance ci) {
				checkCompSynch(ci);
				checkCompPeriod(ci);

				checkEnvCompDataType(ci);
				checkEnvSubComp(ci);
				checkEnvCompHasCD(ci);
				checkNonEnvCompHasCD(ci);
				ContDynamics cd = parseContinuousDynamics(ci);
				checkEnvFlowsDirectReferPort(ci, cd);
				checkEnvFlowsWrongParam(ci, cd);
				checkEnvFlowsWrongDataSubComp(ci, cd);

				checkCompSubDataInitValue(ci);
				checkCompSampleTime(ci);
				checkCompResponseTime(ci);
				checkCompMaxClockDev(ci);

				monitor.worked(1);
				return DONE;
			}

			@Override
			public String caseFeatureInstance(FeatureInstance fi) {
				checkFeatDataOutInitValue(fi);
				checkFeatDataOutParamValue(fi);
				checkCompConnMiss(fi);
				// checkCompInnerConnMiss(fi);

				monitor.worked(1);
				return DONE;
			}

			@Override
			public String caseConnectionInstance(ConnectionInstance ci) {
				checkConnDelayedComp(ci);
				checkConnBetweenEnvComps(ci);
				checkConnEnvOutPortType(ci);

				monitor.worked(1);
				return DONE;
			}
		};
	}

	private void checkCompSynch(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA && !PropertyUtil.isEnvironment(ci)) {
			if (!PropertyUtil.isSynchronous(ci)) {
				getErrorManager().error(ci, ci.getName() + " is not a synchronous " + ci.getCategory().getLiteral());
			}
		}
	}

	private void checkCompPeriod(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA) {
			double period = GetProperties.getPeriodinMS(ci);
			ComponentInstance parent = ci.getContainingComponentInstance();
			if (parent != null) {
				double parentPeriod = GetProperties.getPeriodinMS(parent);
				if (parentPeriod != period) {
					getErrorManager().error(ci, ci.getName() + " have different period with others");
				}
			}
		}
	}

	private void checkEnvSubComp(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			for (ComponentInstance sub : ci.getComponentInstances()) {
				if (sub.getCategory() == ComponentCategory.SYSTEM || sub.getCategory() == ComponentCategory.PROCESS
						|| sub.getCategory() == ComponentCategory.THREAD) {
					getErrorManager().error(ci, ci.getName() + " can not have subcomponent without data component");
				}
			}
		}
	}

	private void checkEnvCompDataType(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			for (ComponentInstance sub : ci.getComponentInstances()) {
				if (sub.getCategory() == ComponentCategory.DATA && !sub.getClassifier().getName().equals("Float")) {
					getErrorManager().error(ci, ci.getName() + " must be float type data component");
				}
			}
		}
	}

	private void checkNonEnvCompHasCD(ComponentInstance ci) {
		if (!PropertyUtil.isEnvironment(ci)
				&& PropertyUtil.hasContinuousDynamics(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.CD)) {
			getErrorManager().error(ci, ci.getName() + " must not have Continuous Dynamics property");
		}
	}

	private void checkEnvCompHasCD(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)
				&& !PropertyUtil.hasContinuousDynamics(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.CD)) {
			getErrorManager().error(ci, ci.getName() + " must have Continuous Dynamics property");
		}
	}

	private ContDynamics parseContinuousDynamics(ComponentInstance ci) {
		if (PropertyUtil.isEnvironment(ci)) {
			String flows = PropertyUtil.getEnvContinuousDynamics(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.CD);
			if (flows == null) {
				return null;
			}
			ANTLRInputStream stream = new ANTLRInputStream(flows);
			ContDynamicsLexer lexer = new ContDynamicsLexer(stream);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			ContDynamicsParser parser = new ContDynamicsParser(tokens);
			ContDynamicsFlowsVisitor visitor = new ContDynamicsFlowsVisitor();
			return visitor.visit(ci, parser.continuousdynamics());
		}
		return null;
	}

	private EList<String> getEnvFlowsVarList(ContDynamics cd) {
		return null;
	}

	private void checkEnvFlowsDirectReferPort(ComponentInstance ci, ContDynamics cd) {
		if (PropertyUtil.isEnvironment(ci) && cd != null) {
			ArrayList<String> featureNames = new ArrayList<String>();
			String ContinuousDynamics = null;
			for (FeatureInstance fi : ci.getFeatureInstances()) {
				featureNames.add(fi.getFeature().getName());
			}
			ContinuousDynamics = PropertyUtil.getEnvContinuousDynamics(ci, PropertyUtil.HYBRIDSYNCHAADL,
					PropertyUtil.CD);
			if (ContinuousDynamics != null) {
				for (String feat : featureNames) {
					if (ContinuousDynamics.contains(feat)) {
						getErrorManager().error(ci, ci.getName()
								+ " should have continuous dynamics without directly using port value");
					}
				}
			}
		}
	}

	private void checkEnvFlowsWrongParam(ComponentInstance ci, ContDynamics cd) {
		if (PropertyUtil.isEnvironment(ci) && cd != null) {

		}
	}

	private void checkEnvFlowsWrongDataSubComp(ComponentInstance ci, ContDynamics cd) {
		if (PropertyUtil.isEnvironment(ci) && cd != null) {

		}
	}

	private void checkCompSubDataInitValue(ComponentInstance ci) {
		if (ci.getCategory() == ComponentCategory.DATA) {
			if (!PropertyUtil.hasDataInitValue(ci)) {
				getErrorManager().error(ci,
						ci.getName() + " does not have a Data_Model::Initial_Value property");
			}
		}
	}

	private void checkCompConnMiss(FeatureInstance fi) {
		ComponentInstance comp = fi.getContainingComponentInstance();
		if (comp.getCategory() == ComponentCategory.SYSTEM && !PropertyUtil.isController(comp)) {
			ComponentInstance container = comp.getContainingComponentInstance();
			for (ConnectionInstance cni : container.getConnectionInstances()) {
				for (InstanceObject io : cni.getThroughFeatureInstances()) {
					if (io.equals(fi)) {
						return;
					}
				}
			}
			getErrorManager().error(fi, fi.getName() + " is not connected to any port");
		}
	}

	private void checkCompInnerConnMiss(FeatureInstance fi) {
		ComponentInstance comp = fi.getContainingComponentInstance();
		GetProperties.getConnectionTiming(fi);
		if (fi.getDirection().outgoing()) {
			EList<ConnectionInstance> out = Aadl2InstanceUtil.getOutgoingConnection(comp, fi);
			if (out == null || out.isEmpty()) {
				getErrorManager().error(fi, fi.getName() + " is not connected to any port");
			}
		} else if (fi.getDirection().incoming()) {
			EList<ConnectionInstance> in = Aadl2InstanceUtil.getIncomingConnection(comp, fi);
			if (in == null || in.isEmpty()) {
				getErrorManager().error(fi, fi.getName() + " is not connected to any port");
			}
		}
	}

	private void checkFeatDataOutInitValue(FeatureInstance fi) {
		ComponentInstance container = fi.getContainingComponentInstance();
		if (!PropertyUtil.isEnvironment(container) && container.getCategory() == ComponentCategory.SYSTEM
				&& !PropertyUtil.isController(container)) {
			if (((DirectedFeature) fi.getFeature()).getDirection().outgoing()
					&& fi.getCategory() == FeatureCategory.DATA_PORT) {
				if (!PropertyUtil.hasDataInitValue(fi)) {
					getErrorManager().error(fi, fi.getName() + " does not have a Data_Model::Initial_Value property");
				}
			}
		}
	}

	private void checkFeatDataOutParamValue(FeatureInstance fi) {
		ComponentInstance container = fi.getContainingComponentInstance();
		if (!PropertyUtil.isEnvironment(container) && container.getCategory() == ComponentCategory.SYSTEM
				&& !PropertyUtil.isController(container)) {
			if (((DirectedFeature) fi.getFeature()).getDirection().outgoing()
					&& fi.getCategory() == FeatureCategory.DATA_PORT && PropertyUtil.hasDataInitValue(fi)
					&& PropertyUtil.hasDataInitValue(fi)) {
				String value = ((StringLiteral) PropertyUtil.getDataInitialValue(fi).getOwnedListElements().get(0))
						.getValue();
				if (value != null && value.equals("param")) {
					getErrorManager().error(fi, fi.getName() + " can not have param initial value");
				}
			}
		}
	}

	private void checkCompSampleTime(ComponentInstance ci) {
		if ((ci.getCategory() == ComponentCategory.SYSTEM || ci.getCategory() == ComponentCategory.THREAD)
				&& !PropertyUtil.isTopComponent(ci)
				&& !PropertyUtil.hasTimeProperty(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.SAMPLING_TIME)) {
			getErrorManager().error(ci, ci.getName() + " does not have a Hybrid_SynchAADL::Sampling_Time");
		}
	}

	private void checkCompResponseTime(ComponentInstance ci) {
		if ((ci.getCategory() == ComponentCategory.SYSTEM || ci.getCategory() == ComponentCategory.THREAD)
				&& !PropertyUtil.isTopComponent(ci)
				&& !PropertyUtil.hasTimeProperty(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.RESPONSE_TIME)) {
			getErrorManager().error(ci, ci.getName() + " does not have a Hybrid_SynchAADL::Response_Time");
		}
	}

	private void checkCompMaxClockDev(ComponentInstance ci) {
		if ((ci.getCategory() == ComponentCategory.SYSTEM)
				&& !PropertyUtil.hasTimeProperty(ci, PropertyUtil.HYBRIDSYNCHAADL, PropertyUtil.MAX_CLOCK_DEV)) {
			getErrorManager().error(ci, ci.getName() + " does not have a Hybrid_SynchAADL::Max_Clock_Deviation");
		}
	}

	private void checkConnDelayedComp(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (!PropertyUtil.isEnvironment(dst) && !PropertyUtil.isEnvironment(src)
				&& !PropertyUtil.isDelayedPortConnection(ci)) {
			getErrorManager().error(ci, ci.getName() + " should be delayed connection");
		}
	}

	private void checkConnBetweenEnvComps(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (PropertyUtil.isEnvironment(src) && PropertyUtil.isEnvironment(dst)) {
			getErrorManager().error(ci, ci.getName() + " is not allowed connection");
		}
	}

	private void checkConnEnvOutPortType(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (PropertyUtil.isEnvironment(src) && !PropertyUtil.isEnvironment(dst)
				&& !PropertyUtil.isDataPortConnection(ci)) {
			getErrorManager().error(ci, ci.getName() + " should be data connection");
		}
	}



}