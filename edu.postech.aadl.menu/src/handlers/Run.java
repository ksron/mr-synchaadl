package handlers;

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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.maude.action.RtmGenerationAction;
import edu.postech.aadl.synch.maude.template.RtmPropSpec;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.xtext.propspec.propSpec.DISTRIBUTED;
import edu.postech.aadl.xtext.propspec.propSpec.Invariant;
import edu.postech.aadl.xtext.propspec.propSpec.MODELCHECK;
import edu.postech.aadl.xtext.propspec.propSpec.Mode;
import edu.postech.aadl.xtext.propspec.propSpec.RANDOM;
import edu.postech.aadl.xtext.propspec.propSpec.Reachability;
import edu.postech.aadl.xtext.propspec.propSpec.SYMBOLIC;
import edu.postech.aadl.xtext.propspec.propSpec.Top;
import maude.Distributed;
import maude.Maude;
import maude.ModelCheck;
import maude.Random;
import maude.Symbolic;

public class Run extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager resManager = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor xtextEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		resManager.setEditor(xtextEditor);
		Top propSpecRes = getPropSpecResource(resManager);

		Action codegenAct = new Action("Constraints Check") {
			@Override
			public void run() {
				if (resManager.getModelResource() != null) {
					RtmGenerationAction act = new RtmGenerationAction();
					act.setTargetPath(resManager.getCodegenFilePath());
					act.selectionChanged(this, new StructuredSelection(resManager.getModelResource()));
					act.run(this);
				} else {
					System.out.println("No AADL instance model!");
				}
			}
		};

		codegenAct.run();

		IPreferenceStore pref = new ScopedPreferenceStore(InstanceScope.INSTANCE, "edu.postech.maude.preferences.page");
		String maudeDirPath = pref.getString("MAUDE_DIRECTORY");
		String maudeExecPath = pref.getString("MAUDE");
		String maudeOptions = pref.getString("MAUDE_OPTIONS");
		System.out.println("maudeDirPath : " + maudeDirPath);
		System.out.println("maudeExecPath : " + maudeExecPath);
		System.out.println("maudeOptions : " + maudeOptions);

		String aadlMaudePath = resManager.getCodegenFilePath().toString();
		String aadlMaudeBaseDir = resManager.getEditorFile().getLocation().removeLastSegments(3).toString();
		String aadlMaudeFullPath = aadlMaudeBaseDir + aadlMaudePath;
		System.out.println("aadlMaudeFullPath : " + aadlMaudeFullPath);

		int idx = 0;
		for (Reachability search : propSpecRes.getReach()) {
			Maude maudes = getMaudeInstance(propSpecRes.getMode());
			maudes.setMaudeDirPath(maudeDirPath);
			maudes.setMaudeExecPath(maudeExecPath);
			maudes.setOption(maudeOptions);

			maudes.setTargetMaude(aadlMaudeFullPath);

			String mode = getMaudeMode(resManager, propSpecRes.getMode());
			maudes.setMode(mode);

			String userFormula = RtmPropSpec.getReachabilityCommand(propSpecRes, search).toString();
			maudes.setUserFormula(userFormula);

			String searchMaudeContents = RtmPropSpec
					.compileReachabilityCommand(propSpecRes, search, propSpecRes.getMode()).toString();
			IPath searchMaudePath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Reachability" + idx + ".maude");
			maudes.writeSearchMaudeFile(searchMaudeContents, searchMaudePath);

			IPath resultPath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Reachability_Result_" + idx + ".txt");
			String nickName = search.getName();
			maudes.runMaude(resultPath, nickName);
			idx += 1;
		}

		idx = 0;
		for (Invariant req : propSpecRes.getInv()) {
			Maude maudes = getMaudeInstance(propSpecRes.getMode());
			maudes.setMaudeDirPath(maudeDirPath);
			maudes.setMaudeExecPath(maudeExecPath);
			maudes.setOption(maudeOptions);

			maudes.setTargetMaude(aadlMaudeFullPath);

			String mode = getMaudeMode(resManager, propSpecRes.getMode());
			maudes.setMode(mode);

			String userFormula = RtmPropSpec.getRequirementCommand(propSpecRes, req).toString();
			maudes.setUserFormula(userFormula);

			String searchMaudeContents = RtmPropSpec.compileRequirementCommand(propSpecRes, req, propSpecRes.getMode())
					.toString();
			IPath searchMaudePath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Requirement" + idx + ".maude");
			maudes.writeSearchMaudeFile(searchMaudeContents, searchMaudePath);

			IPath resultPath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
					.append("Maude_Requirement_Result_" + idx + ".txt");
			String nickName = req.getName();
			maudes.runMaude(resultPath, nickName);
			idx += 1;
		}

		return null;
	}

	private Maude getMaudeInstance(Mode mode) {
		if (mode == null) {
			return new Symbolic();
		} else if (mode instanceof SYMBOLIC) {
			return new Symbolic();
		} else if (mode instanceof RANDOM) {
			return new Random();
		} else if (mode instanceof DISTRIBUTED) {
			return new Distributed();
		} else if (mode instanceof MODELCHECK) {
			return new ModelCheck();
		}
		return new Symbolic();
	}

	private String getMaudeMode(PropspecEditorResourceManager res, Mode mode) {
		String state = "";
		if (mode instanceof SYMBOLIC) {
			state = "smt-check-symbolic";
		} else if (mode instanceof RANDOM) {
			state = "random";
		} else if (mode instanceof DISTRIBUTED) {
			state = "distributed";
		} else if (mode instanceof MODELCHECK) {
			state = "model-check";
		}
		else {
			state = "smt-check-symbolic";
		}
		return res.getEditorFile().getLocation().removeLastSegments(1).append("rtmaude").append("interpreter-" + state)
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
