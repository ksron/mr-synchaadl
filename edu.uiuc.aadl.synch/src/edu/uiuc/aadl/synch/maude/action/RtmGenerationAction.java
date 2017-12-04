package edu.uiuc.aadl.synch.maude.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.osate.aadl2.Element;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.AadlConstants;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.ui.actions.AbstractInstanceOrDeclarativeModelReadOnlyAction;
import org.osate.ui.dialogs.Dialog;
import org.osgi.framework.Bundle;

import com.google.common.collect.HashMultimap;

import edu.uiuc.aadl.synch.Activator;
import edu.uiuc.aadl.synch.maude.template.RtmAadlModel;
import edu.uiuc.aadl.synch.maude.template.RtmAadlSetting;
import edu.uiuc.aadl.utils.IOUtils;


/**
 *
 * @author Kyungmin Bae
 *
 */
public final class RtmGenerationAction extends AbstractInstanceOrDeclarativeModelReadOnlyAction {

	private IPath targetPath;

	public synchronized void setTargetPath(IPath targetPath) {
		this.targetPath = targetPath;
	}

	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	protected String getActionName() {
		return "Real-Time Maude Instance Model Generator";
	}

	@Override
	protected String getMarkerType() {
		return "edu.uiuc.aadl.synch.SyncAadlObjectMarker";
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
			} else {
				Dialog.showInfo(getActionName(), "Code generation succeeded!");
			}
		}
		catch (OperationCanceledException e) {
			Dialog.showInfo(getActionName(), "Code generation canceled!");
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

			final StringBuffer code = new StringBuffer("load " + RtmAadlSetting.SEMANTICS_PATH + "/" + RtmAadlSetting.SEMANTICS_FILE);
			code.append(AadlConstants.newlineChar);
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
		Enumeration<URL> urls = Activator.getDefault().getBundle().findEntries(RtmAadlSetting.SEMANTICS_PATH, "*", true);
		while (urls.hasMoreElements()) {
			URL su = urls.nextElement();
			IFile nfile = IOUtils.getFile(loc.append(su.getFile()));
			if (nfile != null) {
				IOUtils.setFileContent(su.openStream(), nfile);
			}
		}
	}

	@Override
	protected void analyzeDeclarativeModel(IProgressMonitor monitor, AnalysisErrorReporterManager errManager, Element declarativeObject) {
		Dialog.showError(getActionName(), "Please choose an instance model");
	}

}
