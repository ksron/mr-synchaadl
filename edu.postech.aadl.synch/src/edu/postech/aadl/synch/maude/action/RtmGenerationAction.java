package edu.postech.aadl.synch.maude.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
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

import com.google.common.collect.HashMultimap;

import edu.postech.aadl.synch.Activator;
import edu.postech.aadl.synch.maude.template.RtmAadlModel;
import edu.postech.aadl.synch.maude.template.RtmAadlSetting;
import edu.postech.aadl.utils.IOUtils;


/**
 *
 * @author Kyungmin Bae
 *
 */
public final class RtmGenerationAction extends org.osate.ui.handlers.AbstractInstanceOrDeclarativeModelReadOnlyHandler {

	private IPath targetPath;
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

	public synchronized void setTargetPath(IPath targetPath) {
		this.targetPath = targetPath;
	}

	@Override
	protected String getActionName() {
		return "Real-Time Maude Instance Model Generator";
	}

	@Override
	protected String getMarkerType() {
		return "edu.postech.aadl.synch.SyncAadlObjectMarker";
	}

	@Override
	protected void analyzeInstanceModel(IProgressMonitor monitor, AnalysisErrorReporterManager errManager, SystemInstance root, SystemOperationMode som) {

		IPath context = null;
		IPath path = targetPath;
		if (path == null) {
			context = IOUtils.getFile(root.eResource()).getFullPath().removeLastSegments(1);
			path = IOUtils.getCodegenPath(context, root);
		} else {
			context = path.removeLastSegments(1);
		}

		try {
			doCodegen(monitor, errManager, root, context, path);

			if (errManager.getNumErrors() > 0) {
				Dialog.showError(getActionName(), "Some error occured during code generation! Please see the problems view for more details.");
				error = true;
			} else {
				Dialog.showInfo(getActionName(), "Code generation succeeded!");
			}
		}
		catch (OperationCanceledException e) {
			Dialog.showInfo(getActionName(), "Code generation canceled!");
			error = true;
		}
		catch (IOException e) {
			e.printStackTrace();
			Dialog.showError(getActionName(), e.toString());
		}
		catch (CoreException e) {
			e.printStackTrace();
			Dialog.showError(getActionName(), e.toString());
		}
	}


	protected void doCodegen(IProgressMonitor monitor, AnalysisErrorReporterManager errManager, final SystemInstance root, IPath context, IPath path)
			throws CoreException, IOException {

		int count = AadlUtil.countElementsBySubclass(root, ComponentInstance.class);
		monitor.beginTask("Generating a Real-Time Maude instance model", count + 2);

		try {
			HashMultimap<String, String> opTable = HashMultimap.create();
			RtmAadlModel compiler = new RtmAadlModel(monitor, errManager, opTable);

			final StringBuffer code = new StringBuffer();
			code.append(compiler.doGenerate(root));

			monitor.setTaskName("Saving the Real-Time Maude model file...");
			IFile modelFile = IOUtils.getFile(path);
			if (modelFile != null) {
				IOUtils.setFileContent(new ByteArrayInputStream(code.toString().getBytes()), modelFile);
			}
			monitor.worked(1);

			monitor.setTaskName("Copying the Real-Time Maude semantics file...");
			copyMaudeFiles(context);
			monitor.worked(1);
		}
		finally {
			monitor.done();
		}
	}


	private static void copyMaudeFiles(IPath loc) throws IOException, CoreException {
		Enumeration<URL> urls = Activator.getDefault().getBundle().findEntries(RtmAadlSetting.SEMANTICS_PATH, "*.maude",
				true);
		while (urls.hasMoreElements()) {
			URL su = urls.nextElement();
			IFile nfile = IOUtils.getFile(loc.append(su.getFile()));
			if (nfile != null) {
				IOUtils.setFileContent(su.openStream(), nfile);
			}
		}
	}

	@Override
	protected void analyzeDeclarativeModel(IProgressMonitor monitor, AnalysisErrorReporterManager errManager,
			Element declarativeObject) {
		Dialog.showError(getActionName(), "Please choose an instance model");

	}

	@Override
	protected boolean canAnalyzeDeclarativeModels() {
		return false;
	}

}
