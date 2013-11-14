package edu.uiuc.aadl.synch.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.osate.ui.dialogs.Dialog;

import edu.uiuc.aadl.synch.maude.action.RtmVerificationAction;
import edu.uiuc.aadl.synch.propspec.PropspecEditorResourceManager;
import edu.uiuc.aadl.utils.IOUtils;
import edu.uiuc.aadl.xtext.propspec.propSpec.ReqStatement;

public class PropertySectionControl {
	
	protected final static String _TITLE = "AADL Property Requirement"; 

	private final PropspecEditorResourceManager res;
	private TableViewer viewer = null;

	private Action verifyAction = null;
	private Button selectButton = null;
	private Button verifyButton = null;

	protected PropertySectionControl(PropspecEditorResourceManager res) {
		this.res = res;
	}
	
	public void setFocus() {
		verifyButton.setFocus();
	}

	public void setEnabled(boolean enabled) {
		selectButton.setEnabled(enabled);
		verifyAction.setEnabled(enabled && !viewer.getSelection().isEmpty());
		verifyButton.setEnabled(enabled && !viewer.getSelection().isEmpty());
	}
	
	public void update(EList<ReqStatement> reqs) {
		viewer.setInput(reqs);
	}
	
	public TableViewer getViewer() {
		return viewer;
	}

	public Section createControl(FormToolkit toolkit, Composite parent) {
		Section propSec = toolkit.createSection(parent, Section.TITLE_BAR|Section.EXPANDED);
		propSec.setText(_TITLE);
		
		// widgets
		Composite propSecCl = toolkit.createComposite(propSec, SWT.WRAP);
		Table propTable = toolkit.createTable(propSecCl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		selectButton = toolkit.createButton(propSecCl, "Select All", SWT.CHECK);
		selectButton.setSelection(false);
		verifyButton = toolkit.createButton(propSecCl, "Perform Verification", SWT.PUSH);

		// layout
		propSec.setClient(propSecCl);
		propSecCl.setLayout(new GridLayout(2,false));
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true,2,1);  gd.heightHint = propTable.getItemHeight() * 3;
		propTable.setLayoutData(gd);
		verifyButton.setLayoutData(new GridData(SWT.END,SWT.CENTER,false,false));

		addListeners();
		makeViewer(propTable);
		makeVerifyAction();
		
		return propSec;
	}

	private void addListeners() {
		verifyButton.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) { verifyAction.run(); }
		});
		selectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectButton.getSelection())
					viewer.getTable().selectAll();
				else
					viewer.getTable().deselectAll();
				verifyButton.setEnabled(!viewer.getSelection().isEmpty());
			}
		});
	}

	private void makeViewer(final Table propTable) {
		viewer = new TableViewer(propTable);

		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return (inputElement instanceof EList<?>) ? ((EList<?>)inputElement).toArray() : null;  
			}
			@Override public void dispose() { /* Do nothing */ }
			@Override public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { /* Do nothing */ }
		});
		
		viewer.setLabelProvider(new LabelProvider() {
			@Override 
			public String getText(Object element) {
				return (element instanceof ReqStatement) ? ((ReqStatement)element).getName() : element.toString();
			}
			@Override 
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
			}
		});
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				verifyButton.setEnabled(!event.getSelection().isEmpty());
				selectButton.setSelection(viewer.getInput() != null && 
						propTable.getSelectionCount() == propTable.getItemCount());
			}
		});
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object sel = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (sel instanceof ReqStatement) {
					ICompositeNode node = NodeModelUtils.findActualNodeFor((ReqStatement)sel);
					res.getEditor().selectAndReveal(node.getOffset(), node.getLength());
				}
			}
		});
	}

	private void makeVerifyAction() {
		// verify action
		verifyAction = new Action("SynchAADL Verification") {
			@Override
			public void run() {
				IFile efile = res.getEditorFile();
				if (efile != null) {
					RtmVerificationAction act = new RtmVerificationAction();
					act.setReqs(((IStructuredSelection)viewer.getSelection()).toList());
					act.selectionChanged(this, new StructuredSelection(IOUtils.getResource(efile.getFullPath())));
					act.run(this);
				}
				else
					Dialog.showError(getText(), "No Property Specification file!");
			}
		};
	}

}
