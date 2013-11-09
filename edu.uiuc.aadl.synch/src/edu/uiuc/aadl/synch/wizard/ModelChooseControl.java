package edu.uiuc.aadl.synch.wizard;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.workspace.IResourceUtility;


public class ModelChooseControl {
	
	final private class SystemInstanceFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElm, Object elm) {
			if (elm instanceof IResource) {
				if (((IResource)elm).getType() == IResource.FILE)
					return IResourceUtility.isInstanceFile((IResource)elm);
				if (elm instanceof IContainer) {
					try {
						for (IResource mem : ((IContainer)elm).members())
							if (select(viewer, (IContainer)elm, mem)) {
								return true;
							}
					} 
					catch (CoreException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
			return false;
		}
	}
	
	private IResource modelRes = null;
	
	private TreeViewer modelChooser = null;
	private ISelectDialogPage dialog;
	private DestSelectionControl dc;

	protected ModelChooseControl(ISelectDialogPage dialog, DestSelectionControl dc, ISelection selection) {
		this.dialog = dialog;
		this.dc = dc;
		setModel(selection);
	}
	
	public IResource getSelectedResource() {
		return modelRes;
	}
	
	public void init() {
		if (modelRes != null)
			modelChooser.setSelection(new StructuredSelection(modelRes), true);
	}

	public Composite createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		
		Label browseLabel = new Label(container, SWT.NONE);
		browseLabel.setText("&Choose an instance model to verify:");
		browseLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		
		modelChooser = new TreeViewer(container, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);	gd.heightHint = 50;
		modelChooser.getControl().setLayoutData(gd);
		
		// behavior
		modelChooser.setContentProvider(new WorkbenchContentProvider());
		modelChooser.setLabelProvider(new WorkbenchLabelProvider());
		modelChooser.setComparator(new ResourceComparator(ResourceComparator.TYPE));
		modelChooser.setInput(ResourcesPlugin.getWorkspace().getRoot());

		modelChooser.addFilter(new SystemInstanceFilter());
		modelChooser.addSelectionChangedListener(
				new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						setModel(modelChooser.getSelection());
						dc.update(modelRes);
						dialog.dialogChanged();
					}
				});
		return container;
	}
	
	private void setModel(ISelection selection) {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection)selection).getFirstElement();
			if (selected instanceof IResource)
			{
				IResource res = (IResource)selected;
				if (res.getType() == IResource.FILE && AadlUtil.getElement(res) instanceof SystemInstance)
					modelRes = (IResource) selected;
				return;
			}
		}
		modelRes = null;
	}
}
