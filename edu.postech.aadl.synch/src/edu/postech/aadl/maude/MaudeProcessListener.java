package edu.postech.aadl.maude;

import edu.postech.aadl.synch.view.HybridSynchAADLView;

public class MaudeProcessListener {

	private HybridSynchAADLView view;

	public MaudeProcessListener(HybridSynchAADLView view) {
		this.view = view;
	}

	public void update(Maude updatedMaude) {
		view.workbench.getDisplay().asyncExec(() -> view.updateData(updatedMaude, updatedMaude));
	}

}
