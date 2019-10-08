package synchaadlmenu.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.uiuc.aadl.synch.maude.action.RtmGenerationAction;
import edu.uiuc.aadl.synch.propspec.PropspecEditorResourceManager;

public class CodeGeneration extends AbstractHandler {

	private Action codegenAct;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager res = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);


		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.uiuc.aadl.xtext.propspec.PropSpec") == 0)
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
		return null;
	}
}
