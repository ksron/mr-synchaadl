package edu.postech.aadl.synch.checker.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osate.aadl2.Element;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.synch.checker.SynchAadlConstChecker;

public class ConstrainsCheckAction extends org.osate.ui.handlers.AbstractInstanceOrDeclarativeModelReadOnlyHandler {

	private StructuredSelection selection;
	private boolean error = false;
	private Job job;

	public void selectionChanged(StructuredSelection selection) {
		this.selection = selection;
	}

	public void join() {
		try {
			job.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean hasError() {
		return error;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Element root = null;
		root = AadlUtil.getElement(getCurrentSelection(event));
		if (root != null) {
			/*
			 * Here we create the job, and then do two very important things:
			 * (1) set the scheduling rule so that the job locks up the
			 * entire workspace so that nobody messes with it while run.
			 * (2) set the job to be a user job so that we get the
			 * nice progress dialog box AND the option to run the job in the
			 * background.
			 */
			job = createJob(root);
			job.setRule(ResourcesPlugin.getWorkspace().getRoot());
			job.setUser(true); // important!
			job.schedule();
		}
		return null;
	}

	@Override
	protected Object getCurrentSelection(ExecutionEvent event) {
		if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).size() == 1) {
			Object object = ((IStructuredSelection) selection).getFirstElement();
			return object;
		} else {
			return null;
		}
	}

	@Override
	protected String getActionName() {
		return "Synch AADL Constraints Checker";
	}

	@Override
	protected String getMarkerType() {
		return "edu.postech.aadl.synch.SyncAadlObjectMarker";
	}

	@Override
	protected void analyzeInstanceModel(IProgressMonitor monitor,
			AnalysisErrorReporterManager errManager, SystemInstance root, SystemOperationMode som) {
		int count = AadlUtil.countElementsBySubclass(root, ComponentInstance.class);
		try {
			monitor.beginTask(getActionName(), count);
			errManager.addPrefix("Constraints");
			(new SynchAadlConstChecker(monitor,errManager)).processPreOrderAll(root);
		}
		finally {
			monitor.done();
		}

		if (errManager.getNumErrors() > 0) {
			Dialog.showError(getActionName(), "This model violates some synchronous AADL constraints! " +
					"Please see the problems view for more details.");
			error = true;
		} else {
			Dialog.showInfo(getActionName(), "Valid Synchronous AADL model!");
		}
	}

	@Override
	protected void analyzeDeclarativeModel(IProgressMonitor monitor,
			AnalysisErrorReporterManager errManager, Element declarativeObject) {
		Dialog.showError(getActionName(), "Please select an instance model");
	}

	@Override
	protected boolean canAnalyzeDeclarativeModels() {
		return false;
	}
}

