package edu.postech.maude.view.views;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class DisplayView {
	static private final HybridSynchAADLView view = (HybridSynchAADLView) PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow().getActivePage().findView("edu.postech.maude.view.views.MaudeConsoleView");
	static private final Display display = view.workbench.getDisplay();


	public static void clearView() {
		display.asyncExec(() -> view.clear());
	}

	public static void initDataView(MaudeResult init) {
		display.asyncExec(() -> view.initialData(init));
	}

	public static void refreshDataView(MaudeResult oldMR, MaudeResult newMR) {
		display.asyncExec(
				() -> view.refreshData(oldMR, newMR));
	}

}
