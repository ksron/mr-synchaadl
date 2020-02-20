package edu.postech.aadl.synch.menu;

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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osate.ui.dialogs.Dialog;

import edu.postech.aadl.maude.Maude;
import edu.postech.aadl.maude.preferences.MaudePrefPage;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.synch.view.DisplayView;
import edu.postech.aadl.xtext.propspec.propSpec.Property;
import edu.postech.aadl.xtext.propspec.propSpec.Top;

public class SymbolicAnalysis extends AbstractHandler {

	private String maudeDirPath;
	private String maudeExecPath;
	private String maudeOptions;
	private String maudeLibDir;
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
		propSpecFileName = propSpecFileName.substring(0, propSpecFileName.indexOf("."));
		propSpecRes = getPropSpecResource(resManager.getEditorFile().getFullPath());

		IPreferenceStore pref = getValidMaudePref();
		maudeDirPath = pref.getString(MaudePrefPage.MAUDE_DIR);
		maudeExecPath = pref.getString(MaudePrefPage.MAUDE);
		maudeOptions = pref.getString(MaudePrefPage.MAUDE_OPTS);
		maudeLibDir = pref.getString(MaudePrefPage.MAUDE_LIB_DIR);


		aadlMaudePath = resManager.getCodegenFilePath().toString();
		aadlMaudeBaseDir = resManager.getEditorFile().getLocation().removeLastSegments(3).toString();
		aadlMaudeFullPath = aadlMaudeBaseDir + aadlMaudePath;

		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("edu.postech.aadl.view.HybridSynchAADLView");
			DisplayView.setView(view);
			DisplayView.removeData(resManager.getEditorFile());
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		for (Property pr : propSpecRes.getProperty()) {
			Maude maude = new Maude();
			maude = maudeDefaultBuilder(maude);

			IPath pspcGeneratedMaudePath = resManager.getCodegenFilePath().removeLastSegments(1)
					.append(propSpecFileName + "-" + propSpecRes.getName() + "-" + pr.getName() + ".maude");
			maude.setTestFilePath(pspcGeneratedMaudePath);

			IPath resultPath = resManager.getCodegenFilePath().removeLastSegments(1).append("result")
					.append(propSpecFileName + "-" + pr.getName() + ".txt");

			maude.runMaude(resultPath, pr);
		}
		return null;
	}

	private Maude maudeDefaultBuilder(Maude maude) {
		maude.setMaudeDirPath(maudeDirPath);
		maude.setMaudeExecPath(maudeExecPath);
		maude.setOption(maudeOptions);
		maude.setTargetMaude(aadlMaudeFullPath);
		maude.setPspcFile(resManager.getEditorFile());
		maude.setMaudeLibDirPath(maudeLibDir);

		return maude;
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
		String maudeExecPath = pref.getString(MaudePrefPage.MAUDE);
		String maudeOptions = pref.getString(MaudePrefPage.MAUDE_OPTS);
		String maudeLibDir = pref.getString(MaudePrefPage.MAUDE_LIB_DIR);
		if (maudeDirPath.isEmpty()) {
			Dialog.showError("Maude Preferences Error",
					"Maude directory path is not set");
			return null;
		}
		if (maudeExecPath.isEmpty()) {
			Dialog.showError("Maude Preferences Error",
					"Maude path is not set");
			return null;
		}
		if (maudeOptions.isEmpty()) {
			Dialog.showError("Maude Preferences Error",
					"Maude option is not set\n Please set correct Maude option [-no-prelude]");
			return null;
		}
		if (maudeLibDir.isEmpty()) {
			Dialog.showError("Maude Preferences Error",
					"Maude library directory path is not set\n");
			return null;
		}

		return pref;
	}
}