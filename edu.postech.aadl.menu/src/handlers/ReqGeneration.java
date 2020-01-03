package handlers;

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
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.maude.template.RtmPropSpec;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.utils.IOUtils;
import edu.postech.aadl.xtext.propspec.propSpec.Invariant;
import edu.postech.aadl.xtext.propspec.propSpec.Property;
import edu.postech.aadl.xtext.propspec.propSpec.Reachability;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class ReqGeneration extends AbstractHandler {

	private PropspecEditorResourceManager resManager;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		resManager = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor xtextEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		resManager.setEditor(xtextEditor);

		Top propSpecRes = getPropSpecResource(resManager);
		String propSpecFileName = resManager.getEditorFile().getName();
		propSpecFileName = propSpecFileName.substring(0, propSpecFileName.indexOf("."));


		for (Property pr : propSpecRes.getProperty()) {
			if (pr instanceof Reachability) {
				maudeWithReachability(propSpecRes, (Reachability) pr, propSpecFileName);
			} else if (pr instanceof Invariant) {
				maudeWithInvariant(propSpecRes, (Invariant) pr, propSpecFileName);
			} else {
				System.out.println("Not allowed property type");
			}
		}
		return null;
	}

	private void maudeWithReachability(Top propSpecRes, Reachability reach, String propSpecFileName) {
		String userFormulaMaude = RtmPropSpec
				.compilePropertyCommand(propSpecRes, reach, propSpecRes.getMode(), propSpecFileName)
				.toString();
		IPath userFormulaMaudePath = resManager.getCodegenFilePath().removeLastSegments(1)
				.append(propSpecFileName + "-" + propSpecRes.getName() + getName(reach) + ".maude");

		writeSearchMaudeFile(userFormulaMaude, userFormulaMaudePath);
	}


	private void maudeWithInvariant(Top propSpecRes, Invariant inv, String propSpecFileName) {
		String userFormulaMaude = RtmPropSpec
				.compilePropertyCommand(propSpecRes, inv, propSpecRes.getMode(), propSpecFileName)
				.toString();
		IPath userFormulaMaudePath = resManager.getCodegenFilePath().removeLastSegments(1)
				.append(propSpecFileName + "-" + propSpecRes.getName() + getName(inv) + ".maude");

		writeSearchMaudeFile(userFormulaMaude, userFormulaMaudePath);
	}

	private String getName(Property pr) {
		if (pr.getName() != null) {
			return "-" + pr.getName();
		}
		return "";
	}

	public void writeSearchMaudeFile(String txt, IPath path) {
		IFile maudeSearchFile = IOUtils.getFile(path);
		try {
			IOUtils.setFileContent(new ByteArrayInputStream(txt.getBytes()), maudeSearchFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private Top getPropSpecResource(PropspecEditorResourceManager res) {
		IPath path = res.getEditorFile().getFullPath().removeLastSegments(0);

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