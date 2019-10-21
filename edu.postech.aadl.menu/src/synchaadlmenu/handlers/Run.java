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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.maude.action.RtmGenerationAction;
import edu.postech.aadl.synch.maude.template.RtmPropSpec;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.xtext.propspec.propSpec.Requirement;
import edu.postech.aadl.xtext.propspec.propSpec.Search;
import edu.postech.aadl.xtext.propspec.propSpec.Top;
import edu.postech.maude.view.views.MaudeConsoleView;
import edu.postech.xtext.maude.MaudeResult;
import edu.postech.xtext.maude.MaudeRunner;

public class Run extends AbstractHandler {

	private Action codegenAct;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager res = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		res.setEditor(newEditor);

		codegenAct = new Action("Constraints Check") {
			@Override
			public void run() {
				if (res.getModelResource() != null) {
					RtmGenerationAction act = new RtmGenerationAction();
					act.setTargetPath(res.getCodegenFilePath());
					act.selectionChanged(this, new StructuredSelection(res.getModelResource()));
					act.run(this);
				} else {
					System.out.println("No AADL instance model!");
				}
			}
		};

		codegenAct.run();

		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView("edu.postech.maude.view.views.MaudeConsoleView");

		Top spec = getPropSpecResource(res);

		String TargetMaudePath = res.getCodegenFilePath().toString();
		String BaseLocation = res.getEditorFile().getLocation().removeLastSegments(3).toString();


		IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"edu.postech.maude.preferences.page");
		String runMaudeDirectory = store.getString("MAUDE_DIRECTORY");
		String runMaude = store.getString("MAUDE");
		String runMaudeOptions = store.getString("MAUDE_OPTIONS");

		int idx = 0;

		List<MaudeResult> Lmr = new ArrayList<MaudeResult>();

		for (Search search : spec.getSearch()) {
			MaudeRunner maudes = new MaudeRunner();
			maudes.setMaudeDirectory(runMaudeDirectory);
			maudes.setUserCommand(RtmPropSpec.getReachabilityCommand(spec, search).toString());
			maudes.setName(runMaude);
			maudes.setOption(runMaudeOptions);
			maudes.setMode(getMaudeMode(res, spec.getMode()));
			maudes.setTargetMaude(BaseLocation + TargetMaudePath);
			maudes.makeMaudeFile(RtmPropSpec.compileReachabilityCommand(spec, search).toString(),
					res.getCodegenFilePath().removeLastSegments(1).append("result")
							.append("Maude_Reachability" + idx + ".maude"));
			String rm = maudes.runMaude(res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Reachability_Result_" + idx + ".txt"));

			IPath path3 = res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Reachability_Result_" + idx + ".txt");
			Lmr.add(new MaudeResult(search.getNickname(), resultMaude(rm), path3.toString(), "1.0ms"));
			idx++;
		}

		idx = 0;

		for (Requirement req : spec.getRequirement()) {
			MaudeRunner maudes = new MaudeRunner();
			maudes.setMaudeDirectory(runMaudeDirectory);
			maudes.setUserCommand(RtmPropSpec.getRequirementCommand(spec, req).toString());
			maudes.setName(runMaude);
			maudes.setOption(runMaudeOptions);
			maudes.setMode(getMaudeMode(res, spec.getMode()));
			maudes.setTargetMaude(BaseLocation + TargetMaudePath);
			maudes.makeMaudeFile(RtmPropSpec.compileRequirementCommand(spec, req).toString(), res.getCodegenFilePath()
					.removeLastSegments(1).append("result").append("Maude_Requirement" + idx + ".maude"));
			String rm = maudes.runMaude(res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Requirement_Result_" + idx + ".txt"));

			IPath path2 = res.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Requirement_Result_" + idx + ".txt");
			Lmr.add(new MaudeResult(req.getNickname(), resultMaude(rm), path2.toString(), "1.0ms"));
			idx++;
		}

		((MaudeConsoleView) view).refreshData(Lmr);

		return null;
	}

	public String resultMaude(String rm) {
		if (rm.indexOf("No solution") != -1) {
			return "UnReachable";
		} else if (rm.indexOf("Solution 1") != -1) {
			return "Reachable";
		} else {
			return "Error Occured!";
		}
	}

	private String getMaudeMode(PropspecEditorResourceManager res, String mode) {
		return res.getEditorFile().getLocation().removeLastSegments(1).append("rtmaude").append("interpreter-" + mode)
				.addFileExtension("maude").toString();
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
