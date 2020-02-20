package edu.postech.aadl.menu;

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.maude.preferences.MaudePrefPage;
import edu.postech.aadl.synch.maude.action.RtmGenerationAction;
import edu.postech.aadl.synch.maude.template.RtmPropSpec;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.utils.IOUtils;
import edu.postech.aadl.xtext.propspec.propSpec.Property;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class CodeGeneration extends AbstractHandler {
	private boolean error = false;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager resManager = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);


		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		resManager.setEditor(newEditor);

		IPreferenceStore pref = getValidMaudePref();
		if (pref == null) {
			error = true;
			return null;
		}
		String maudeDirPath = pref.getString(MaudePrefPage.MAUDE_DIR);

		if (resManager.getModelResource() != null) {
			RtmGenerationAction act = new RtmGenerationAction();
			act.setTargetPath(resManager.getCodegenFilePath());
			act.selectionChanged(new StructuredSelection(resManager.getModelResource()));
			act.execute(event);
			act.join();
			act.hasError();
		} else {
			throw new ExecutionException("No AADL instance model!");
		}


		Top propSpecRes = getPropSpecResource(resManager.getEditorFile().getFullPath());
		String propSpecFileName = resManager.getEditorFile().getName();
		propSpecFileName = propSpecFileName.substring(0, propSpecFileName.indexOf("."));

		for (Property pr : propSpecRes.getProperty()) {
			String userFormulaMaude = RtmPropSpec
					.compilePropertyCommand(propSpecRes, pr, propSpecRes.getMode(), maudeDirPath).toString();
			IPath userFormulaMaudePath = resManager.getCodegenFilePath().removeLastSegments(1)
					.append(propSpecFileName + "-" + propSpecRes.getName() + "-" + pr.getName() + ".maude");
			writeAnalysisMaudeFile(userFormulaMaude, userFormulaMaudePath);
		}

		return null;
	}

	public boolean isError() {
		return error;
	}

	public void writeAnalysisMaudeFile(String txt, IPath path) {
		IFile maudeSearchFile = IOUtils.getFile(path);
		try {
			IOUtils.setFileContent(new ByteArrayInputStream(txt.getBytes()), maudeSearchFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
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

	private IPreferenceStore getValidMaudePref() {
		IPreferenceStore pref = new ScopedPreferenceStore(InstanceScope.INSTANCE, "edu.postech.maude.preferences.page");
		String maudeDirPath = pref.getString(MaudePrefPage.MAUDE_DIR);
		if (maudeDirPath.isEmpty()) {
			Dialog.showError("Maude Preferences Error",
					"Maude directory is not found\n Please set correct Maude directory");
			return null;
		}
		return pref;
	}

}
