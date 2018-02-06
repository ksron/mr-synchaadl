package edu.uiuc.aadl.synch.maude.action;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.osate.ui.dialogs.Dialog;

import edu.uiuc.aadl.synch.maude.template.RtmPropSpec;
import es.upv.dsic.issi.moment.maudesimpleGUI.MaudesimpleGUIPlugin;
import es.upv.dsic.issi.moment.maudesimpleGUI.core.Maude;

public class RtmSimulationAction implements IActionDelegate {

	final public static int NO_BOUND = -1;
	private int bound = NO_BOUND;

	private Object currentSelection = null;

	public void setBound(int bound) {
		this.bound = bound;
	}

	@Override
	public void run(IAction action) {
		if (bound < 0) {
			Dialog.showError(action.getText(), "No simulation bound!");
			return;
		}

		if (currentSelection instanceof IResource) {
			Maude maude = MaudesimpleGUIPlugin.getDefault().getMaude();
			if (!maude.isRunning()) {
				maude.runMaude();
			}

			String osName = System.getProperty("os.name").toLowerCase();

			if (osName.contains("win")) {
				// maude runs in cygwin, which has default path /cygdrive/ prior to the original path
				maude.sendToMaude("load " + "/cygdrive/"
						+ ((IResource) currentSelection).getLocation().toString().replace(":", "") + "\n");
			} else {
				maude.sendToMaude("load " + ((IResource) currentSelection).getLocation().toOSString() + "\n");
			}

			maude.sendToMaude(RtmPropSpec.compileSimulCommand(bound).toString());
		}
		else {
			Dialog.showError(action.getText(), "No Real-Time Maude model!");
		}
	}

	@Override
	public synchronized void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection && !((IStructuredSelection)selection).isEmpty() ) {
			currentSelection = ((IStructuredSelection)selection).getFirstElement();
		}
	}

}
