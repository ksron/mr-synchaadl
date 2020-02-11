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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.maude.template.RtmPropSpec;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.xtext.propspec.propSpec.Invariant;
import edu.postech.aadl.xtext.propspec.propSpec.Mode;
import edu.postech.aadl.xtext.propspec.propSpec.Property;
import edu.postech.aadl.xtext.propspec.propSpec.Reachability;
import edu.postech.aadl.xtext.propspec.propSpec.Top;
import maude.Maude;
import maude.Symbolic;

public class Run extends AbstractHandler {

	private String maudeDirPath;
	private String maudeExecPath;
	private String maudeOptions;
	private String aadlMaudePath;
	private String aadlMaudeBaseDir;
	private String aadlMaudeFullPath;

	private Top propSpecRes;
	private String propSpecFileName;

	private PropspecEditorResourceManager resManager;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		resManager = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor xtextEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		resManager.setEditor(xtextEditor);

		propSpecFileName = resManager.getEditorFile().getName();
		propSpecRes = getPropSpecResource(resManager);

		IPreferenceStore pref = new ScopedPreferenceStore(InstanceScope.INSTANCE, "edu.postech.maude.preferences.page");
		maudeDirPath = pref.getString("MAUDE_DIRECTORY");
		maudeExecPath = pref.getString("MAUDE");
		maudeOptions = pref.getString("MAUDE_OPTIONS");
		System.out.println("maudeDirPath : " + maudeDirPath);
		System.out.println("maudeExecPath : " + maudeExecPath);
		System.out.println("maudeOptions : " + maudeOptions);

		aadlMaudePath = resManager.getCodegenFilePath().toString();
		aadlMaudeBaseDir = resManager.getEditorFile().getLocation().removeLastSegments(3).toString();
		aadlMaudeFullPath = aadlMaudeBaseDir + aadlMaudePath;
		System.out.println("aadlMaudeFullPath : " + aadlMaudeFullPath);

		for (Property pr : propSpecRes.getProperty()) {
			if (pr instanceof Reachability) {
				maudeWithReachability((Reachability) pr);

			} else if (pr instanceof Invariant) {
				maudeWithInvariant((Invariant) pr);
			} else {
				System.out.println("Not allowed property type");
			}
		}
		return null;
	}

	private void maudeWithReachability(Reachability reach) {
		Maude maude = new Symbolic();
		maude = maudeDefaultBuilder(maude);

		String itptrPath = getMaudeInterpreterPath(propSpecRes.getMode());
		maude.setMode(itptrPath);

		String userFormula = RtmPropSpec.getReachabilityCommand(propSpecRes, reach).toString();
		maude.setUserFormula(userFormula);

		String userFormulaMaude = RtmPropSpec.compilePropertyCommand(propSpecRes, reach, propSpecRes.getMode(), "")
				.toString();

		IPath userFormulaMaudePath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
				.append(getStringMergeOption(propSpecRes.getMode())).append("_" + reach.getName() + ".maude");
		// maude.writeSearchMaudeFile(userFormulaMaude, userFormulaMaudePath);

		IPath resultPath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
				.append(getStringMergeOption(propSpecRes.getMode())).append("_" + reach.getName() + ".txt");

		String nickName = "not implemented";
		maude.runMaude(resultPath, nickName);
	}

	private void maudeWithInvariant(Invariant inv) {
		Maude maude = new Symbolic();
		maude = maudeDefaultBuilder(maude);

		String itptrPath = getMaudeInterpreterPath(propSpecRes.getMode());
		maude.setMode(itptrPath);


		maude.setRequirement(true);

		String userFormulaMaude = RtmPropSpec.compilePropertyCommand(propSpecRes, inv, propSpecRes.getMode(), "")
				.toString();

		IPath userFormulaMaudePath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
				.append(getStringMergeOption(propSpecRes.getMode())).append("_" + inv.getName() + ".maude");
		// maude.writeSearchMaudeFile(userFormulaMaude, userFormulaMaudePath);

		IPath resultPath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
				.append(getStringMergeOption(propSpecRes.getMode())).append("_" + inv.getName() + ".txt");

		String nickName = "not implemented";
		maude.runMaude(resultPath, nickName);
	}

	private Maude maudeDefaultBuilder(Maude maude) {
		maude.setMaudeDirPath(maudeDirPath);
		maude.setMaudeExecPath(maudeExecPath);
		maude.setOption(maudeOptions);
		maude.setTargetMaude(aadlMaudeFullPath);

		return maude;
	}

	private String getMaudeInterpreterPath(Mode mode) {
		String state = getStringMergeOption(mode);
		return resManager.getEditorFile().getLocation().removeLastSegments(1).append("semantics")
				.append("interpreter-" + state)
				.addFileExtension("maude").toString();
	}

	private String getStringMergeOption(Mode mode) {
		String interpreter = "symbolic";
		return interpreter;
	}

	private String getMaudeInterpreterPath(String state) {
		return resManager.getEditorFile().getLocation().removeLastSegments(1).append("semantics")
				.append("interpreter-" + state).addFileExtension("maude").toString();
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