package edu.postech.maude.view.views;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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

		TableViewerColumn tvc0 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc0.getColumn().setText("Nickname");
		tvc0.getColumn().setResizable(true);
		tvc0.getColumn().setMoveable(true);
		tvc0.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.nickname;
			}
		});

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

		LinkOpener linkHandler = rowObject -> {
			MaudeResult mr = (MaudeResult)rowObject;
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IPath path = new Path(mr.location);
			IFile ifile = root.getFile(path);
			try {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), ifile);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		};

		tvc2.setLabelProvider(new LinkLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.location;
			}
		}, linkHandler));

		TableViewerColumn tvc3 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc3.getColumn().setText("ElapsedTime");
		tvc3.getColumn().setResizable(true);
		tvc3.getColumn().setMoveable(true);
		tvc3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.elapsedTime;
			}
		});

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(2, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));

		viewer.getTable().setLayout(layout);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		List<MaudeResult> mrList = new ArrayList<MaudeResult>();
		mrList.add(new MaudeResult("nickname 1", "Result 1", "Location 1", "1.0ms"));
		mrList.add(new MaudeResult("nickname 2", "Result 2", "Location 2", "2.0ms"));
		mrList.add(new MaudeResult("nickname 3", "Result 3", "Location 3", "3.0ms"));

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(mrList);

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
				IPath path = new Path(mr.location);
				path = path.removeLastSegments(2);
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				try {
					for (IResource resource : ((IContainer) root.findMember(path)).members()) {
						if (resource.getName().contains(".pspc")) {
							IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
									(IFile) resource);
						}
					}
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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

	public void refreshData(List<MaudeResult> mrList) {
		// System.out.println("Refresh Data : " + viewer);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(mrList);
		getSite().setSelectionProvider(viewer);

	}

}
