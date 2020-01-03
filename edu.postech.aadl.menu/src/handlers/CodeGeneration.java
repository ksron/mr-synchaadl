package handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.maude.action.RtmGenerationAction;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class CodeGeneration extends AbstractHandler {

	private Action codegenAct;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager res = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);


		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		res.setEditor(newEditor);
		IPath path = pspcBasedCodegenFilePath(res.getCodegenFilePath(), res);
		System.out.println("Path Debug : " + path);

		codegenAct = new Action("Constraints Check") {
			@Override
			public void run() {
				if (res.getModelResource() != null) {
					RtmGenerationAction act = new RtmGenerationAction();
					act.setTargetPath(path);
					act.selectionChanged(this, new StructuredSelection(res.getModelResource()));
					act.run(this);
				} else {
					System.out.println("No AADL instance model!");
				}
			}
		};

		codegenAct.run();
		return null;
	}

	private IPath pspcBasedCodegenFilePath(IPath path, PropspecEditorResourceManager resManager) {
		if (resManager != null) {
			String propSpecFileName = resManager.getEditorFile().getName();
			propSpecFileName = propSpecFileName.substring(0, propSpecFileName.indexOf("."));
			String lastSegment = path.lastSegment();
			return path.removeLastSegments(1).append(propSpecFileName + "-" + lastSegment);
		}
		return null;
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
