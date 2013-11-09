package edu.uiuc.aadl.synch.views;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osate.ui.dialogs.Dialog;

import edu.uiuc.aadl.synch.maude.action.RtmSimulationAction;
import edu.uiuc.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.uiuc.aadl.utils.IOUtils;

public class SimulSectionControl {

	protected final static String _TITLE = "Real-Time Maude Simulation"; 

	private final PropspecEditorResourceManager res;
	
	private Action simulAction;
	private Text simulText = null;
	private Button simulButton = null;
	
	protected SimulSectionControl(PropspecEditorResourceManager res) {
		this.res = res;
	}

	public void setFocus() {
		simulButton.setFocus();
	}

	public void setEnabled(boolean enabled) {
		simulText.setEnabled(enabled);
		simulAction.setEnabled(enabled && !simulText.getText().isEmpty());
		simulButton.setEnabled(enabled && !simulText.getText().isEmpty());
	}
	
	public void clear() {
		simulText.setText("");
	}

	public Section createControl(FormToolkit toolkit, Composite parent) {
		Section simulSec = toolkit.createSection(parent, Section.TITLE_BAR|Section.EXPANDED);
		simulSec.setText(_TITLE);
		
		// widgets
		Composite simulSecCl = toolkit.createComposite(simulSec);
		toolkit.createLabel(simulSecCl, "Bound:");
		simulText = toolkit.createText(simulSecCl, "");
		simulButton = toolkit.createButton(simulSecCl, "Perform Simulation", SWT.PUSH);
		
		// layout
		simulSec.setClient(simulSecCl);
		simulSecCl.setLayout(new GridLayout(2,false));
		simulText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		simulButton.setLayoutData(new GridData(SWT.END,SWT.CENTER,false,false,2,1));
		
		addListeners();
		makeSimulAction();
		
		return simulSec;
	}

	private void addListeners() {
		// behavior
		simulButton.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) { simulAction.run(); }
		});
		simulText.addModifyListener(new ModifyListener() {
			@Override public void modifyText(ModifyEvent e) { simulButton.setEnabled(!simulText.getText().isEmpty()); }
		});
	}

	private void makeSimulAction() {
		// action
		simulAction = new Action("SynchAADL Simulation") {
			@Override
			public void run() {
				try {
					int bound = Integer.parseInt(simulText.getText());
					IPath path = res.getCodegenFilePath();
					IResource rtm = IOUtils.getResource(path);
					if (rtm != null) {
						RtmSimulationAction act = new RtmSimulationAction();
						act.setBound(bound);
						act.selectionChanged(this, new StructuredSelection(rtm));
						act.run(this);
					}
					else
						Dialog.showError(getText(), "No Real-Time Maude model!");
				} catch (NumberFormatException ne) {
					Dialog.showError(getText(), "Not Number!");
				}
			}

		};
	}

}
