package edu.postech.aadl.synch.menu;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.maude.Maude;
import edu.postech.aadl.maude.MaudeController;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.xtext.propspec.propSpec.Property;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class SymbolicAnalysis extends AbstractHandler {
	private PropspecEditorResourceManager resManager;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		resManager = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor xtextEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		resManager.setEditor(xtextEditor);

		IPath pspcFilePath = resManager.getEditorFile().getFullPath();
		String pspcFileName = pspcFilePath.removeFileExtension().lastSegment();
		Top propSpecRes = getPropSpecResource(pspcFilePath);

		MaudeController controller = new MaudeController();
		controller.setView("edu.postech.aadl.view.HybridSynchAADLView");
		controller.refreshView(getPSPCFile());

		for (Property prop : propSpecRes.getProperty()) {
			Maude maude = new Maude(getPSPCFile(), prop, "Running..", getResultTextPath(pspcFileName, prop.getName()), "ms");
			controller.initDataView(maude);
			controller.runMaude(getPropertyMaudePath(pspcFileName, propSpecRes.getName(), prop.getName()), maude);
		}
		return null;
	}

	private IFile getPSPCFile() {
		return resManager.getEditorFile();
	}

	private IPath getResultTextPath(String pspcFileName, String propId) {
		return resManager.getCodegenFilePath().removeLastSegments(1).append("result")
				.append(pspcFileName + "-" + propId + ".txt");
	}

	private IPath getPropertyMaudePath(String pspcFileName, String maudeInstanceName, String propId) {
		return resManager.getCodegenFilePath().removeLastSegments(1)
				.append(pspcFileName + "-" + maudeInstanceName + "-" + propId + ".maude");
	}

	private Top getPropSpecResource(IPath path) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IResource ire = root.findMember(path);

		ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.getResource(URI.createURI(ire.getFullPath().toString()), true);
		EObject eo = resource.getContents().get(0);
		if (eo instanceof Top) {
			return (Top) eo;
		}
		return null;
	}
}