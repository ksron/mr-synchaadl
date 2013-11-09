package edu.uiuc.aadl.synch.views;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.uiuc.aadl.synch.propspec.PropspecEditorResourceManager;


/**
 * 
 * @author kquine
 *
 */
public class SynchAadlView extends ViewPart implements ISelectionListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.uiuc.aadl.synch.views.SynchAADLView";
	
	/**
	 * The PropSpec xtext editor attached to this view 
	 */
	private PropspecEditorResourceManager res = new PropspecEditorResourceManager();
	
	private ScrolledForm form = null;
	private ModelSectionControl modelSC = new ModelSectionControl(res);
	private SimulSectionControl simulSC = new SimulSectionControl(res);
	private PropertySectionControl propSC = new PropertySectionControl(res);
	
	@Override
	public void setFocus() {
		modelSC.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (getViewSite().getPage().isPartVisible(this)) {
			XtextEditor newEditor = 
					(part.getSite().getId().compareTo("edu.uiuc.aadl.xtext.propspec.PropSpec")==0)
					&& (part instanceof XtextEditor) ?  (XtextEditor)part : null;
					
			switch (res.setEditor(newEditor)) {
				case CHANGED:
					updateView(newEditor); break;
				case CLEARED:
					clearView(); break;
				case NOT_CHANGED:
					break;
			}
		}
	}

	private void updateView(XtextEditor newEditor) {
		form.setEnabled(true);		modelSC.setEnabled(true);	
		simulSC.setEnabled(true);	propSC.setEnabled(true);
		modelSC.update(res.getContent().getPath(), res.getEditor().getTitle());
		propSC.update(res.getContent().getRequirements());
	}

	private void clearView() {
		form.setEnabled(false);		modelSC.setEnabled(false);
		simulSC.setEnabled(false);	propSC.setEnabled(false);
		modelSC.update(null, "");	simulSC.clear();
		propSC.update(null);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		
		// sections
		Section modelSec = modelSC.createControl(toolkit, form.getBody());
		Section simulSec = simulSC.createControl(toolkit, form.getBody());
		Section propSec = propSC.createControl(toolkit, form.getBody());
		
		// layout
		form.getBody().setLayout(new GridLayout());
		modelSec.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		simulSec.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		propSec.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
		// register actions 
		getViewSite().getActionBars().getMenuManager().add(modelSC.getCheckAction());	
		getViewSite().getActionBars().getMenuManager().add(modelSC.getCodegenAction());	
		getViewSite().getActionBars().getToolBarManager().add(modelSC.getCheckAction());	
		getViewSite().getActionBars().getToolBarManager().add(modelSC.getCodegenAction());	
	
		// register the selection listener: "edu.uiuc.aadl.xtext.propspec.PropSpec"?
		getSite().getPage().addPostSelectionListener(this);
		
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getContent(), "edu.uiuc.aadl.synch.viewer");
		
		clearView();
	}


}