package edu.uiuc.aadl.synch.checker.action;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.Element;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.actions.AbstractInstanceOrDeclarativeModelReadOnlyAction;
import org.osate.ui.dialogs.Dialog;
import org.osgi.framework.Bundle;

import edu.uiuc.aadl.synch.Activator;
import edu.uiuc.aadl.synch.checker.SynchAadlConstChecker;

public class ConstrainsCheckAction extends AbstractInstanceOrDeclarativeModelReadOnlyAction {

	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	protected String getActionName() {
		return "Synch AADL Constraints Checker";
	}

	@Override
	protected String getMarkerType() {
		return "edu.uiuc.aadl.synch.SyncAadlObjectMarker";
	}

	@Override
	protected void analyzeInstanceModel(IProgressMonitor monitor,
			AnalysisErrorReporterManager errManager, SystemInstance root, SystemOperationMode som) {
		int count = AadlUtil.countElementsBySubclass(root, ComponentInstance.class);
		try {
			monitor.beginTask(getActionName(), count);
			(new SynchAadlConstChecker(monitor,errManager)).processPreOrderAll(root);
		}
		finally {
			monitor.done();
		}

		if (errManager.getNumErrors() > 0) {
			Dialog.showError(getActionName(), "This model violates some synchronous AADL constraints! " +
					"Please see the problems view for more details.");
		} else {
			Dialog.showInfo(getActionName(), "Valid Synchronous AADL model!");
		}
	}

	@Override
	protected void analyzeDeclarativeModel(IProgressMonitor monitor,
			AnalysisErrorReporterManager errManager, Element declarativeObject) {
		Dialog.showError(getActionName(), "Please select an instance model");
	}
}

