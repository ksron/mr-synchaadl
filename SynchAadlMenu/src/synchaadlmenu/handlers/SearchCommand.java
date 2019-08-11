package synchaadlmenu.handlers;

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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.xtext.maude.MaudeRunner;
import edu.postech.xtext.maude.maude.Model;
import edu.uiuc.aadl.synch.maude.template.RtmPropSpec;
import edu.uiuc.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.uiuc.aadl.xtext.propspec.propSpec.Top;

public class SearchCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager res = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.uiuc.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		res.setEditor(newEditor);

		Top spec = getPropSpecResource(res);

		String TargetMaudePath = res.getCodegenFilePath().toString();
		String BaseLocation = res.getEditorFile().getLocation().removeLastSegments(3).toString();

		Model model = getMaudeConfigurationResource(res);

		// Maude Handler
		MaudeRunner maudes = new MaudeRunner();
		maudes.setDirectory(model.getPath());
		maudes.setName(model.getMaude());
		maudes.setOption(model.getOptions());
		maudes.setMode(getMaudeMode(res, spec.getMode()));
		maudes.setTargetMaude(BaseLocation + TargetMaudePath);
		maudes.makeMaudeFile(RtmPropSpec.compileSearchCommand(spec).toString());
		System.out.println(maudes.DebugCompileCommand());
		maudes.runMaude();

		return null;
	}

	private String getMaudeMode(PropspecEditorResourceManager res, String mode) {
		return res.getEditorFile().getLocation().removeLastSegments(1).append("rtmaude")
				.append("interpreter-" + mode).addFileExtension("maude").toString();
	}


	private Model getMaudeConfigurationResource(PropspecEditorResourceManager res) {
		IPath path = res.getEditorFile().getFullPath().removeLastSegments(1).append("Maude_Configuration")
				.addFileExtension("config");

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IResource ire = root.findMember(path);

		ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.getResource(URI.createURI(ire.getFullPath().toString()), true);
		EObject eo = resource.getContents().get(0);
		if (eo instanceof Model) {
			return (Model) eo;
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