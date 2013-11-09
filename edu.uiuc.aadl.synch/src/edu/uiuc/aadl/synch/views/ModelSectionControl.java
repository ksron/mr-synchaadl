package edu.uiuc.aadl.synch.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osate.ui.dialogs.Dialog;

import edu.uiuc.aadl.synch.checker.action.ConstrainsCheckAction;
import edu.uiuc.aadl.synch.maude.action.RtmGenerationAction;
import edu.uiuc.aadl.synch.propspec.PropspecEditorResourceManager;

public class ModelSectionControl {
	
	protected final static String _TITLE = "AADL Property Spec"; 
	
	private final PropspecEditorResourceManager res;

	private Action checkAct = null;
	private Action codegenAct = null;

	private Text specText = null;			
	private Button checkButton = null;
	private Button codegenButton = null;
	
	protected ModelSectionControl(PropspecEditorResourceManager res) {
		this.res = res;
	}

	public void setFocus() {
		checkButton.setFocus();
	}

	public void setEnabled(boolean enabled) {
		checkAct.setEnabled(enabled);
		codegenAct.setEnabled(enabled);
		specText.setEnabled(enabled);
		checkButton.setEnabled(enabled);
		codegenButton.setEnabled(enabled);
	}
	
	public void update(String path, String spec)
	{
		specText.setText(spec);
	}
	
	public Action getCheckAction()
	{
		return checkAct;
	}
	public Action getCodegenAction()
	{
		return codegenAct;
	}

	public Section createControl(FormToolkit toolkit, Composite parent) {
		Section modelSec = toolkit.createSection(parent, Section.TITLE_BAR|Section.EXPANDED);
		modelSec.setText(_TITLE);
		
		Composite modelSecCl = toolkit.createComposite(modelSec);
		modelSecCl.setLayout(new GridLayout(2, false));
		modelSec.setClient(modelSecCl);
		
		toolkit.createLabel(modelSecCl, "Spec:");
		specText = toolkit.createText(modelSecCl, "", SWT.BORDER);	specText.setEditable(false);
		GridData gd = new GridData(SWT.FILL,SWT.CENTER,true,false); gd.widthHint = 50;
		specText.setLayoutData(gd);
		
		Composite buttons = toolkit.createComposite(modelSecCl);
		GridLayout lo = new GridLayout(2, false);	lo.marginWidth = 0;
		buttons.setLayout(lo);
		buttons.setLayoutData(new GridData(SWT.END,SWT.CENTER,false,false,2,1));
		
		checkButton = toolkit.createButton(buttons, "Constraints Check", SWT.PUSH);
		codegenButton = toolkit.createButton(buttons, "Code Generation", SWT.PUSH);
		
		addListeners();
		makeCheckAction();
		makeCodegenAction();
		
		return modelSec;
	}

	private void addListeners() {
		// behavior
		checkButton.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) { checkAct.run(); }
		});
		codegenButton.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) { codegenAct.run(); }
		});
	}

	private void makeCheckAction() {
		// check action
		checkAct = new Action("Constraints Check") {
			@Override 
			public void run() {
				if (res.getModelResource() != null) {
					ConstrainsCheckAction chkAct = new ConstrainsCheckAction();
					chkAct.selectionChanged(this, new StructuredSelection(res.getModelResource()));
					chkAct.run(this);
				}
				else
					Dialog.showError(getText(), "No AADL instance model!");
			}
		};
		checkAct.setToolTipText("Check Synch AADL constraints");
		checkAct.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJS_TASK_TSK));
	}

	private void makeCodegenAction() {
		// codegen action
		codegenAct = new Action("Code Generation") {
			@Override 
			public void run() {
				if (res.getModelResource() != null) {
					RtmGenerationAction act = new RtmGenerationAction();
					act.setTargetPath(res.getCodegenFilePath());
					act.selectionChanged(this, new StructuredSelection(res.getModelResource()));
					act.run(this);
				}
				else
					Dialog.showError(getText(), "No AADL instance model!");
			}
		};
		codegenAct.setToolTipText("Generate the Real-Time Maude model");
		codegenAct.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(org.eclipse.ui.ide.IDE.SharedImages.IMG_OPEN_MARKER));
	}


}
