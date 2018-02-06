package edu.uiuc.aadl.synch.maude.action;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.osate.ui.dialogs.Dialog;

import edu.uiuc.aadl.synch.maude.template.RtmPropSpec;
import edu.uiuc.aadl.utils.IOUtils;
import edu.uiuc.aadl.xtext.propspec.propSpec.ReqStatement;
import edu.uiuc.aadl.xtext.propspec.propSpec.Top;
import es.upv.dsic.issi.moment.maudesimpleGUI.MaudesimpleGUIPlugin;
import es.upv.dsic.issi.moment.maudesimpleGUI.core.Maude;

public class RtmVerificationAction implements IActionDelegate {

	private Object currentSelection = null;
	private List<ReqStatement> targetReqs = null;

	@Override
	public void run(IAction action) {
		try {
			if (currentSelection instanceof IResource) {
				IResource res = (IResource)currentSelection;
				Top spec = getPropSpecTop(res);

				IPath path = res.getFullPath().removeFileExtension().addFileExtension("verification.maude");
				IFile file = IOUtils.getFile(path);
				if (file != null) {
					createSpecCodeAsJob(spec, file);
				}

				Maude maude = MaudesimpleGUIPlugin.getDefault().getMaude();
				if (!maude.isRunning()) {
					maude.runMaude();
				}

				// pick the selected requirements (all by default)
				List<ReqStatement> targets = targetReqs;
				if (targets == null || targets.isEmpty()) {
					targets = spec.getRequirements();
				}

				String osName = System.getProperty("os.name").toLowerCase();

				if (osName.contains("win")) {
					// maude runs in cygwin, which has default path /cygdrive/ prior to the original path
					maude.sendToMaude("cd " + "/cygdrive/"
							+ file.getLocation().removeLastSegments(1).toString().replace(":", "") + "\n");
					maude.sendToMaude("load " + "/cygdrive/" + file.getLocation().toString().replace(":", "") + "\n");
				} else {
					maude.sendToMaude("cd " + file.getLocation().removeLastSegments(1).toOSString() + "\n");
					maude.sendToMaude("load " + file.getLocation().toOSString() + "\n");
				}

				for (ReqStatement rq : targets)
				{
					maude.sendToMaude(RtmPropSpec.compileReqCommand(rq).toString());
				}
			} else {
				Dialog.showError(action.getText(), "No Property Specification file!");
			}
		} catch (CoreException e) {
			e.printStackTrace();
			Dialog.showError(action.getText(), e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			Dialog.showError(action.getText(), e.getMessage());
		}
	}

	private void createSpecCodeAsJob(final Top spec, final IFile file)
			throws CoreException, InterruptedException {
		WorkspaceJob job = new WorkspaceJob("Real-Time Maude Property Spec Generation") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("Generating a Real-Time Maude property spec", 2);
				try {
					CharSequence specCode = RtmPropSpec.compileSpec(spec);
					monitor.worked(1);

					monitor.setTaskName("Saving the Real-Time Maude model file...");
					IOUtils.setFileContent(new ByteArrayInputStream(specCode.toString().getBytes()), file);
					monitor.worked(1);
				}
				finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setUser(true);
		job.schedule();
		job.join();
	}

	private Top getPropSpecTop(IResource res) {
		ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.getResource(URI.createURI(res.getFullPath().toString()), true);
		EObject eo = resource.getContents().get(0);
		if (eo instanceof Top) {
			return (Top)eo;
		}
		return null;
	}


	public void setReqs(List<?> reqs) {
		if (reqs != null) {
			this.targetReqs = new ArrayList<ReqStatement>();
			for(Object o : reqs) {
				if (o instanceof ReqStatement) {
					this.targetReqs.add((ReqStatement)o);
				}
			}
		}
	}

	@Override
	public synchronized void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection && !((IStructuredSelection)selection).isEmpty() ) {
			currentSelection = ((IStructuredSelection)selection).getFirstElement();
		}
	}
}
