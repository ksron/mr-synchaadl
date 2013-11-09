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
				checkConnSrc(ci);
				checkConnDst(ci);
				checkConnTiming(ci);				
				return DONE;
			}
		};
	}
	
	private void checkCompSynch(ComponentInstance ci) {
		if (ci.getCategory() != ComponentCategory.DATA)
		{
			if(! PropertyUtil.isSynchronous(ci))
				getErrorManager().error(ci, ci.getName() + " is not a synchronous " + ci.getCategory().getLiteral());
		}
	}

	private void checkCompPeriod(ComponentInstance ci, ComponentInstance parent) {
		if (ci.getCategory() != ComponentCategory.DATA)
		{
			double period = GetProperties.getPeriodinMS(ci);
			if (period == 0.0)
				getErrorManager().error(ci, ci.getName() + " does not define a period");
			else
			{
				if (parent != null && GetProperties.getPeriodinMS(parent) % period > 0)
					getErrorManager().error(ci, ci.getName() + " has a period that is not a multiple of its parent.");
			}
		}
	}

	private void checkThreadDispatchProtocol(ComponentInstance ci) {
		if (ci.getCategory() == ComponentCategory.THREAD) {
			EnumerationLiteral dp = GetProperties.getDispatchProtocol(ci);
			if (dp == null || !dp.getName().equalsIgnoreCase(AadlProject.PERIODIC_LITERAL))
				getErrorManager().error(ci, ci.getName() + " is not periodic");
		}
	}

	private void checkConnDataPort(ConnectionInstance ci) {
		if (!PropertyUtil.isDataPortConnection(ci))
			getErrorManager().error(ci, ci.getName() + " is not an data port connection");
	}

	private void checkConnSrc(ConnectionInstance ci) {
		ComponentInstance src = PropertyUtil.getSrcComponent(ci);
		if (src == null || src.getCategory() != ComponentCategory.THREAD) 
			getErrorManager().error(ci, ci.getName() + " is invalid; src must be a non-null thread");
	}
	
	private void checkConnDst(ConnectionInstance ci) {
		ComponentInstance dst = PropertyUtil.getDstComponent(ci);
		if (dst == null || dst.getCategory() != ComponentCategory.THREAD)
			getErrorManager().error(ci, ci.getName() + " is invalid; dst must be a non-null thread");
	}

	private void checkConnTiming(ConnectionInstance ci) {
		if (! PropertyUtil.isDelayedPortConnection(ci))
		{
			getErrorManager().error(ci, ci.getName() + " is not delayed");
		}
	}
	

}
