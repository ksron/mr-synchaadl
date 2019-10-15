package edu.postech.aadl.builder.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.checker.action.ConstrainsCheckAction;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;

public class AADLModelInstanceBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				checkAADLInstanceModel();
				// handle added resource
				break;
			case IResourceDelta.REMOVED:
				checkAADLInstanceModel();
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				checkAADLInstanceModel();
				// handle changed resource
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		@Override
		public boolean visit(IResource resource) {
			checkAADLInstanceModel();
			//return true to continue visiting children.
			return true;
		}
	}

	public static final String BUILDER_ID = "edu.postech.aadl.builder.AADLModelInstanceBuilder";

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor)
			throws CoreException {
		int idx = 1;
		while (true) {
			System.out.println("Build : AADLModelInstanceBuilder");
			if (idx == 0) {
				break;
			}
		}
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}


	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}

	private void checkAADLInstanceModel() {
		Action checkAct;
		for (IWorkbenchWindow iwbw : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage iwbp : iwbw.getPages()) {
				IWorkbenchPart part = iwbp.getActivePart();
				XtextEditor newEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0) && (part instanceof XtextEditor) ? (XtextEditor) part : null;
				if (newEditor != null) {
					System.out.println("XtextEditor : " + newEditor);
					PropspecEditorResourceManager res = new PropspecEditorResourceManager();
					res.setEditor(newEditor);
					checkAct = new Action("Constraints Check") {
						@Override
						public void run() {
							if (res.getModelResource() != null) {
								ConstrainsCheckAction chkAct = new ConstrainsCheckAction();
								chkAct.selectionChanged(this, new StructuredSelection(res.getModelResource()));
								chkAct.run(this);
							} else {
								System.out.println("No AADL instance model!");
							}
						}
					};
					checkAct.run();
				}
			}
		}
	}
}
