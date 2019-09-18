package edu.uiuc.aadl.synch.checker;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.DirectedFeature;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.RangeValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.FeatureCategory;
import org.osate.aadl2.instance.FeatureInstance;
import org.osate.aadl2.instance.util.InstanceSwitch;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.xtext.aadl2.properties.util.AadlProject;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.uiuc.aadl.synch.maude.template.RtmAadlSetting;
import edu.uiuc.aadl.utils.PropertyUtil;

public class SynchAadlConstChecker extends AadlProcessingSwitchWithProgress {

	private double period = 0.0;

	public SynchAadlConstChecker(IProgressMonitor pm, AnalysisErrorReporterManager errMgr) {
		super(pm, errMgr);
	}

	@Override
	protected void initSwitches() {
		instanceSwitch = new InstanceSwitch<String>() {

			@Override
			public String caseComponentInstance(ComponentInstance ci) {
				// System.out.println("[Debug] " + ci.getName());
				// checkCompSynch(ci);

				checkSinglePeriod(ci);
				checkSamePeriod(ci);
				checkDataInitialValue(ci);
				checkOutDataPort(ci);

				checkMaxSamplingTime(ci);
				checkMaxResponseTime(ci);

				checkRealFloatVariable(ci);
				checkContinuousDynamicsVar(ci);

				// checkThreadDispatchProtocol(ci);
				monitor.worked(1);
				return DONE;
			}

			@Override
			public String caseConnectionInstance(ConnectionInstance ci) {
				// System.out.println("[Debug] " + ci.getName());

				checkImmediateDelayedConnectionEnv(ci);

				// checkConnDataPort(ci);
				// checkConnEventDataPort(ci);
				// checkConnSrc(ci);
				// checkConnDst(ci);
				// checkConnTiming(ci);
				return DONE;
			}
		};
	}

	// Hybrid AADL Constraints
	// 1. Common
	// A. All components must have a single period value
	private void checkSinglePeriod(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA) {
			double period = GetProperties.getPeriodinMS(ci);
			if (period == 0.0) {
				getErrorManager().error(ci, ci.getName() + " does not define a period");
			} else if(this.period == 0.0){
				this.period = period;
			}
		}
	}

	// 1. Common
	// B. All components should have a same period value
	private void checkSamePeriod(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA) {
			double period = GetProperties.getPeriodinMS(ci);
			if (this.period != period) {
				getErrorManager().error(ci, ci.getName() + " does not have a same period");
			}
		}
	}

	// 1. Common
	// C. All data components must have an initial value (Data_Model::Initial_Value)
	private void checkDataInitialValue(ComponentInstance ci) {
		boolean check = false;
		if (ci.getCategory() == ComponentCategory.DATA) {
			for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
				if (pa.getProperty().getNamespace().getName().equals(PropertyUtil.DATA_MODEL)
						&& pa.getProperty().getName().equals(PropertyUtil.INITIAL_VALUE)) {
					check = true;
				}
			}
			if (!check) {
				getErrorManager().error(ci,
						ci.getName() + " does not have a Data_Model::Initial_Value property in data component");
			}
		}
	}

	// 1. Common
	// D. All out data port to a controller component / system component must have an initial value
	private void checkOutDataPort(ComponentInstance ci) {
		boolean check = false;
		if (ci.getCategory() == ComponentCategory.SYSTEM && !RtmAadlSetting.isEnv(ci)) {
			for (FeatureInstance fi : ci.getFeatureInstances()) {
				if (((DirectedFeature) fi.getFeature()).getDirection().outgoing()
						&& fi.getCategory().compareTo(FeatureCategory.DATA_PORT) == 0) {
					for (PropertyAssociation pa : fi.getOwnedPropertyAssociations()) {
						if (pa.getProperty().getNamespace().getName().equals(PropertyUtil.DATA_MODEL)
								&& pa.getProperty().getName().equals(PropertyUtil.INITIAL_VALUE)) {
							check = true;
						}
					}
					if (!check) {
						getErrorManager().error(ci,
								ci.getName() + " does not have a Data_Model::Initial_Value property in out data port");
					}

				}
			}

		}
	}

	// 2. Controller Component
	// A. Ports
	// i. Output port to physical environment: event data/ event port

	// 2. Controller Component
	// A. Ports
	// ii. input port: data port

	// 2. Controller Component
	// B. Connections
	// i. Connection between data port must be delayed connection

	// 2. Controller Component
	// C. Timing
	// i. Controllers must have a max_clock_deviation, sampling_/response_time properties

	// 2. Controller Component
	// C. Timing
	// ii. The max sampling time must not exceed the min response timing
	private void checkMaxSamplingTime(ComponentInstance ci) {
		long MaxSamplingTime = -1;
		long MinResponseTime = -1;
		for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
			if (pa.getProperty().getName().contains(PropertyUtil.SAMPLING_TIME)) {
				MaxSamplingTime = ((IntegerLiteral) ((RangeValue) PropertyUtils.getSimplePropertyValue(ci,
						pa.getProperty())).getMaximum()).getValue();
			}
			if (pa.getProperty().getName().contains(PropertyUtil.RESPONSE_TIME)) {
				MinResponseTime = ((IntegerLiteral) ((RangeValue) PropertyUtils.getSimplePropertyValue(ci,
						pa.getProperty())).getMaximum()).getValue();
			}
		}
		if (MaxSamplingTime != -1 && MinResponseTime != -1 && MaxSamplingTime > MinResponseTime) {
			getErrorManager().error(ci, ci.getName() + " has max sampling time larger than min response time");
		}
	}

	// 2. Controller Component
	// C. Timing
	// iii. The max response timing must not exceed the period
	private void checkMaxResponseTime(ComponentInstance ci) {
		long MaxResponseTime = -1;
		double period = GetProperties.getPeriodinMS(ci);
		for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
			if (pa.getProperty().getName().contains(PropertyUtil.RESPONSE_TIME)) {
				MaxResponseTime = ((IntegerLiteral) ((RangeValue) PropertyUtils.getSimplePropertyValue(ci,
						pa.getProperty())).getMaximum()).getValue();
			}
		}
		if (period != 0.0 && MaxResponseTime != -1 && MaxResponseTime > period) {
			getErrorManager().error(ci, ci.getName() + " has max response time larger than period");
		}
	}


	// 2. Controller Component
	// C. Timing
	// iv. Max clock deviation

	// 3. Physical Environment Component
	// A. Ports
	// i. Output port to another physical controller: data port
	// 1. Output port should be directly connected to controller, meaning that its value must not be connected to the outer component

	// 3. Physical Environment Component
	// A. Ports
	// ii. Input port
	// 1. From controller component: event data / event port

	// 3. Physical Environment Component
	// B. Connections
	// i. By non-periodic characteristic of physical environment
	// 1. Immediate / delayed connection input not allowed
	private void checkImmediateDelayedConnectionEnv(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (RtmAadlSetting.isEnv(dst) || RtmAadlSetting.isEnv(src)) {
			if (PropertyUtil.isDelayedPortConnection(ci)) {
				getErrorManager().error(ci, ci.getName() + " isn't allowed to be delayed");
			}
			if (PropertyUtil.isImmediatePortConnection(ci)) {
				getErrorManager().error(ci, ci.getName() + " isn't allowed to be immediate");
			}
		}
	}

	// 3. Physical Environment Component
	// B. Connections
	// ii. Input port should be connected to the data component to be used in the continuousDynamics


	// 3. Physical Environment Component
	// B. Connections
	// iii. Output port should be connected to the data component


	// 3. Physical Environment Component
	// C. Mode
	// i. By syntax) transitions are triggered by inputs from event data / event port


	// 3. Physical Environment Component
	// D. DataComponent
	// i. Represents real variable: must have real / float type
	private void checkRealFloatVariable(ComponentInstance ci) {
		boolean check = false;
		if (RtmAadlSetting.isEnv(ci)) {
			for (ComponentInstance cci : ci.getComponentInstances()) {
				if (cci.getCategory() == ComponentCategory.DATA) {
					if (cci.getClassifier().getName().equals("Float") || cci.getClassifier().getName().equals("Real")) {
						check = true;
					}
				}
			}
			if (!check) {
				getErrorManager().error(ci, ci.getName() + " must have real / float type data component");
			}
		}
	}


	// 3. Physical Environment Component
	// E. Property
	// i. Hybrid_SynchAADL::isEnvironment must be set to true


	// 3. Physical Environment Component
	// F. Hybrid_SynchAADL::ContinuousDynamics
	// i. In the form of "variable (differential variable) = formula"


	// 3. Physical Environment Component
	// F. Hybrid_SynchAADL::ContinuousDynamics
	// ii. Formula must not directly use input / output port value. It should only reference input / output port through data subcomponents connected to each
	// port.
	private void checkContinuousDynamicsVar(ComponentInstance ci) {
		ArrayList<String> featureNames = new ArrayList<String>();
		String ContinuousDynamics = null;
		if (RtmAadlSetting.isEnv(ci)) {
			for (FeatureInstance fi : ci.getFeatureInstances()) {
				featureNames.add(fi.getFeature().getName());
			}
			for (PropertyAssociation pa : ci.getOwnedPropertyAssociations()) {
				if (pa.getProperty().getQualifiedName().contains(PropertyUtil.CD)) {
					ContinuousDynamics = ((StringLiteral) pa.getOwnedValues().get(0).getOwnedValue()).getValue();
				}
			}
			if (ContinuousDynamics != null) {
				for (String str : featureNames) {
					if (ContinuousDynamics.contains(str)) {
						getErrorManager().error(ci,
								ci.getName() + " should have continuous dynamics not to use input / output port value");
					}
				}
			}
		}
	}

	// Not used in this time.
	/************************/
	private void checkCompSynch(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA && !PropertyUtil.isEnvironment(ci))
		{
			if(! PropertyUtil.isSynchronous(ci)) {
				getErrorManager().error(ci, ci.getName() + " is not a synchronous " + ci.getCategory().getLiteral());
			}
		}
	}



	private void checkThreadDispatchProtocol(ComponentInstance ci) {
		if (ci.getCategory() == ComponentCategory.THREAD && !PropertyUtil.isEnvironment(ci)) {
			EnumerationLiteral dp = GetProperties.getDispatchProtocol(ci);
			if (dp == null || !dp.getName().equalsIgnoreCase(AadlProject.PERIODIC_LITERAL)) {
				getErrorManager().error(ci, ci.getName() + " is not periodic");
			}
		}
	}

	/************************/
	private void checkConnDataPort(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (!PropertyUtil.isDataPortConnection(ci)) {
			if ((PropertyUtil.isEnvironment(src) && PropertyUtil.isEnvironment(dst))
					|| !PropertyUtil.isEnvironment(dst)) {
				getErrorManager().error(ci, ci.getName() + " is not an data port connection");
			}
		}
	}

	private void checkConnEventDataPort(ConnectionInstance ci) {
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (PropertyUtil.isEnvironment(dst)
				&& (!PropertyUtil.isEventDataPortConnection(ci) || !PropertyUtil.isEventPortConnection(ci))) {
			getErrorManager().error(ci, ci.getName() + " is not an event data / eventport connection");
		}
	}

	private void checkConnSrc(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		if (!PropertyUtil.isEnvironment(src) && (src == null || src.getCategory() != ComponentCategory.THREAD)) {
			getErrorManager().error(ci, ci.getName() + " is invalid; src must be a non-null thread");
		}
	}

	private void checkConnDst(ConnectionInstance ci) {
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (!PropertyUtil.isEnvironment(dst) && (dst == null || dst.getCategory() != ComponentCategory.THREAD)) {
			getErrorManager().error(ci, ci.getName() + " is invalid; dst must be a non-null thread");
		}
	}

	private void checkConnTiming(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (!PropertyUtil.isEnvironment(src) && !PropertyUtil.isEnvironment(dst)) {
			if (!PropertyUtil.isDelayedPortConnection(ci)) {
				getErrorManager().error(ci, ci.getName() + " is not delayed");
			}
		}
		else {
			if(PropertyUtil.isDelayedPortConnection(ci) || PropertyUtil.isImmediatePortConnection(ci)) {
				getErrorManager().error(ci, ci.getName() + " is not non-deterministic");
			}
		}
	}


}
