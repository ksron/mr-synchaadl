package synchaadlmenu.handlers;

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.maude.action.RtmGenerationAction;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.postech.aadl.utils.IOUtils;

public class CodeGeneration extends AbstractHandler {

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
		IPath path = res.getCodegenFilePath().removeLastSegments(1).append("Maude_Configuration.config");
		System.out.println("Debug : " + path);
		makeMaudeConfigFile(path);
		return null;
	}

	private void makeMaudeConfigFile(IPath path) {
		IFile xtextFile = IOUtils.getFile(path);
		String code = "Maude Directory : \"/Users/jaehun/dropbox/research/Maude-Alpha\";\n" + "\n"
				+ "Maude: \"maude-Yices2.darwin64\";\n" + "\n"
				+ "Options : \"-no-prelude\" \"prelude.maude\" \"smt.maude\";";

		try {
			IOUtils.setFileContent(new ByteArrayInputStream(code.toString().getBytes()), xtextFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
