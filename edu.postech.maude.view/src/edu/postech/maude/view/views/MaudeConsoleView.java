package edu.postech.maude.view.views;


import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import edu.postech.xtext.maude.MaudeResult;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class MaudeConsoleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.postech.maude.view.views.MaudeConsoleView";

	@Inject IWorkbench workbench;

	private TableViewer viewer;
	private Action doubleClickAction;
	private List<IPath> paths;

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);


		TableViewerColumn tvc1 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc1.getColumn().setText("Result");
		tvc1.getColumn().setResizable(true);
		tvc1.getColumn().setMoveable(true);
		tvc1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.result;
			}
		});

		TableViewerColumn tvc2 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc2.getColumn().setText("Location");
		tvc2.getColumn().setResizable(true);
		tvc2.getColumn().setMoveable(true);
		tvc2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.name;
			}
		});

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(2, true));
		layout.addColumnData(new ColumnWeightData(1, true));

		viewer.getTable().setLayout(layout);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		// List<MaudeResult> mrList = new ArrayList<MaudeResult>();
		// mrList.add(new MaudeResult("Test1", "Test2"));
		// mrList.add(new MaudeResult("Test3", "Test4"));
		// mrList.add(new MaudeResult("Test5", "Test6"));

		// viewer.setContentProvider(new ArrayContentProvider());
		// viewer.setInput(mrList);

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "edu.postech.maude.view.viewer");
		getSite().setSelectionProvider(viewer);

		makeActions();
		hookDoubleClickAction();
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				MaudeResult mr = (MaudeResult) obj;

				System.out.println("mr location : " + mr.name);

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IPath path = new Path(mr.name);
				IFile ifile = root.getFile(path);

				try {
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), ifile);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//iuieditor.open(URI.createURI(ire.getFullPath().toString()), true);
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(event -> doubleClickAction.run());
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void refreshData(List<MaudeResult> mrList, List<IPath> paths) {
		// System.out.println("Refresh Data : " + viewer);
		this.paths = paths;
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(mrList);
		getSite().setSelectionProvider(viewer);

	}

}
