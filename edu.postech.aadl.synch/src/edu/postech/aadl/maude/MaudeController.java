package edu.postech.aadl.maude;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.postech.aadl.synch.view.HybridSynchAADLView;

public class MaudeController {
	private HybridSynchAADLView view;

	public void runMaude(IPath propPath, Maude maude) {
		MaudeProcess process = new MaudeProcess(maude, propPath);
		process.setListener(new MaudeProcessListener(view));
		process.start();
	}

	public void setView(String viewId) {
		try {
			view = (HybridSynchAADLView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(viewId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void initDataView(Maude maude) {
		view.workbench.getDisplay().asyncExec(() -> view.initialData(maude));
	}

	public void refreshView(IFile pspcFile) {
		view.workbench.getDisplay().asyncExec(() -> view.removeData(pspcFile));
	}
}
