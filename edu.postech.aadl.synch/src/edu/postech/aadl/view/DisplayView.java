package edu.postech.aadl.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;

import edu.postech.aadl.xtext.propspec.propSpec.Property;

public class DisplayView {
	static private HybridSynchAADLView view;
	static private Display display;

	public static void setView(IViewPart viewPart) {
		view = (HybridSynchAADLView) viewPart;
		display = view.workbench.getDisplay();
	}

	public static void clearView() {
		display.asyncExec(() -> view.clear());
	}

	public static void initDataView(MaudeResult init) {
		display.asyncExec(() -> view.initialData(init));
	}

	public static void refreshData(EList<Property> prop) {
		display.asyncExec(() -> view.refreshData(prop));
	}

	public static void removeData(IPath path) {
		display.asyncExec(() -> view.removeData(path));
	}

	public static void removeData(IFile prop) {
		display.asyncExec(() -> view.removeData(prop));
	}

	public static void updateDataView(MaudeResult oldMR, MaudeResult newMR) {
		display.asyncExec(
				() -> view.updateData(oldMR, newMR));
	}

}
