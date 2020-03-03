package edu.postech.aadl.synch.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;

import edu.postech.aadl.maude.Maude;

public class DisplayView {
	static private HybridSynchAADLView view;
	static private Display display;

	public static void setView(IViewPart viewPart) {
		view = (HybridSynchAADLView) viewPart;
		display = view.workbench.getDisplay();
	}

	public static void removeData(IFile prop) {
		display.asyncExec(() -> view.removeData(prop));
	}

	public static void initDataView(Maude init) {
		display.asyncExec(() -> view.initialData(init));
	}

	public static void updateDataView(Maude oldMR, Maude newMR) {
		display.asyncExec(
				() -> view.updateData(oldMR, newMR));
	}

}
