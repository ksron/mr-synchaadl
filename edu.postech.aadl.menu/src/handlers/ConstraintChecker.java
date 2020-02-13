package handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.postech.aadl.synch.checker.action.ConstrainsCheckAction;
import edu.postech.aadl.synch.propspec.PropspecEditorResourceManager;

public class ConstraintChecker extends AbstractHandler {

	private Action checkAct;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PropspecEditorResourceManager res = new PropspecEditorResourceManager();
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		XtextEditor newEditor = (part.getSite().getId().compareTo("edu.postech.aadl.xtext.propspec.PropSpec") == 0)
				&& (part instanceof XtextEditor) ? (XtextEditor) part : null;

		res.setEditor(newEditor);

		checkAct = new Action("Constraints Check") {
			@Override
			public void run() {
				if (res.getModelResource() != null) {
					ConstrainsCheckAction chkAct = new ConstrainsCheckAction();
					chkAct.selectionChanged(this, new StructuredSelection(res.getModelResource()));
					chkAct.run(this);
				} else {
					System.out.println("No AADL instance model!");
				}
			}
		};
		checkAct.run();
		return null;
	}
}
