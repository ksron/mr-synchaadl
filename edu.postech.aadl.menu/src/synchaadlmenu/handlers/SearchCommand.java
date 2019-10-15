package synchaadlmenu.handlers;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.maude.template.RtmPropSpec;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.xtext.propspec.propSpec.Requirement;
import edu.postech.aadl.xtext.propspec.propSpec.Search;
import edu.postech.aadl.xtext.propspec.propSpec.Top;
import edu.postech.maude.view.views.MaudeConsoleView;
import edu.postech.xtext.maude.MaudeResult;
import edu.postech.xtext.maude.MaudeRunner;
import edu.postech.xtext.maude.maude.Model;

public class SearchCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager res = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView("edu.postech.maude.view.views.MaudeConsoleView");

		res.setEditor(newEditor);

		Top spec = getPropSpecResource(res);

		String TargetMaudePath = res.getCodegenFilePath().toString();
		String BaseLocation = res.getEditorFile().getLocation().removeLastSegments(3).toString();

		Model model = getMaudeConfigurationResource(res);
		// System.out.println("Search commands : " + spec.getSearch());
		int idx = 0;

		List<MaudeResult> Lmr = new ArrayList<MaudeResult>();
		List<IPath> paths = new ArrayList<IPath>();

		for(Search search : spec.getSearch()) {
			MaudeRunner maudes = new MaudeRunner();
			maudes.setMaudeDirectory(model.getPath());
			maudes.setName(model.getMaude());
			maudes.setOption(model.getOptions());
			maudes.setMode(getMaudeMode(res, spec.getMode()));
			maudes.setTargetMaude(BaseLocation + TargetMaudePath);
			maudes.makeMaudeFile(RtmPropSpec.compileReachabilityCommand(spec, search).toString(),
					res.getCodegenFilePath().removeLastSegments(1).append("result")
							.append("Maude_Reachability" + idx + ".maude"));
			System.out.println(RtmPropSpec.compileReachabilityCommand(spec, search).toString());
			// System.out.println(maudes.DebugCompileCommand());
			String rm = maudes.runMaude(res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Reachability_Result_" + idx + ".txt"));

			IPath path = res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Reachability_Result_" + idx + ".txt");
			paths.add(path);
			Lmr.add(new MaudeResult(resultMaude(rm), path.toString()));

			idx++;
		}

		idx = 0;

		for (Requirement req : spec.getRequirement()) {
			MaudeRunner maudes = new MaudeRunner();
			maudes.setMaudeDirectory(model.getPath());
			maudes.setName(model.getMaude());
			maudes.setOption(model.getOptions());
			maudes.setMode(getMaudeMode(res, spec.getMode()));
			maudes.setTargetMaude(BaseLocation + TargetMaudePath);
			maudes.makeMaudeFile(RtmPropSpec.compileRequirementCommand(spec, req).toString(),
					res.getCodegenFilePath()
							.removeLastSegments(1).append("result").append("Maude_Requirement" + idx + ".maude"));
			System.out.println(RtmPropSpec.compileRequirementCommand(spec, req).toString());
			// System.out.println(maudes.DebugCompileCommand());
			String rm = maudes.runMaude(res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Requirement_Result_" + idx + ".txt"));

			IPath path = res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Requirement_Result_" + idx + ".txt");
			paths.add(path);
			Lmr.add(new MaudeResult(resultMaude(rm), path.toString()));

			idx++;
		}

		((MaudeConsoleView) view).refreshData(Lmr, paths);

		return null;
	}

	public String resultMaude(String rm) {
		if (rm.indexOf("No solution") != -1) {
			return "No solution";
		} else if (rm.indexOf("Solution 1") != -1) {
			return "Solution";
		} else {
			return "Error Occured!";
		}
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