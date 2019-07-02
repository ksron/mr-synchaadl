package edu.uiuc.aadl.synch.checker;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.util.InstanceSwitch;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.xtext.aadl2.properties.util.AadlProject;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import edu.uiuc.aadl.utils.PropertyUtil;

public class SynchAadlConstChecker extends AadlProcessingSwitchWithProgress {

	//TODO: check input adators for multirate connections (e.g., existance, fast/slow types, etc.)
	//TODO: should initial values be a vector for the fast machine?, or given by the related input adaptors??

	public SynchAadlConstChecker(IProgressMonitor pm, AnalysisErrorReporterManager errMgr) {
		super(pm, errMgr);
	}

	@Override
	protected void initSwitches() {
		instanceSwitch = new InstanceSwitch<String>() {

			@Override
			public String caseComponentInstance(ComponentInstance ci) {
				checkCompSynch(ci);
				checkCompPeriod(ci, ci.getContainingComponentInstance());
				checkThreadDispatchProtocol(ci);
				monitor.worked(1);
				return DONE;
			}

			@Override
			public String caseConnectionInstance(ConnectionInstance ci) {
				checkConnDataPort(ci);
				checkConnEventDataPort(ci);
				checkConnSrc(ci);
				checkConnDst(ci);
				checkConnTiming(ci);
				return DONE;
			}
		};
	}

	/************************/
	private void checkCompSynch(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA && !PropertyUtil.isEnvironment(ci))
		{
			if(! PropertyUtil.isSynchronous(ci)) {
				getErrorManager().error(ci, ci.getName() + " is not a synchronous " + ci.getCategory().getLiteral());
			}
		}
	}

	private void checkCompPeriod(ComponentInstance ci, ComponentInstance parent) {
		if (ci.getCategory() != ComponentCategory.DATA && !PropertyUtil.isEnvironment(ci))
		{
			double period = GetProperties.getPeriodinMS(ci);
			if (period == 0.0) {
				getErrorManager().error(ci, ci.getName() + " does not define a period");
			} else
			{
				if (parent != null && GetProperties.getPeriodinMS(parent) % period > 0) {
					getErrorManager().error(ci, ci.getName() + " has a period that is not a multiple of its parent.");
				}
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

	/*
	 * private void checkCompEnv(ComponentInstance ci) {
	 * if (ci.getCategory() == ComponentCategory.DATA && PropertyUtil.isEnvironment(ci)) {
	 * GetProperties.getBaseType(ci);
	 * if () {
	 * getErrorManager().error(ci, ci.getName() + "");
	 * }
	 * }
	 * }
	 */
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
