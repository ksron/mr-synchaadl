package edu.postech.maude.view.views;


import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import edu.postech.aadl.xtext.propspec.propSpec.Property;

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

public class HybridSynchAADLView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.postech.maude.view.views.MaudeConsoleView";

	@Inject
	public IWorkbench workbench;

	private TableViewer viewer;
	private Action doubleClickAction;
	private Action stopProcessAction;
	private Action deleteResultAction;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		TableViewerColumn tvc0 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc0.getColumn().setText("PSPC File");
		tvc0.getColumn().setResizable(true);
		tvc0.getColumn().setMoveable(true);
		tvc0.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.getPspcFileName();
			}
		});

		TableViewerColumn tvc1 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc1.getColumn().setText("Property Id");
		tvc1.getColumn().setResizable(true);
		tvc1.getColumn().setMoveable(true);
		tvc1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.getPropId();
			}
		});

		TableViewerColumn tvc2 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc2.getColumn().setText("Result");
		tvc2.getColumn().setResizable(true);
		tvc2.getColumn().setMoveable(true);
		tvc2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.getResultString();
			}
		});

		TableViewerColumn tvc3 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc3.getColumn().setText("Location");
		tvc3.getColumn().setResizable(true);
		tvc3.getColumn().setMoveable(true);

		LinkOpener linkHandler = rowObject -> {
			MaudeResult mr = (MaudeResult)rowObject;
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IPath path = new Path(mr.getLocationString());
			IFile ifile = root.getFile(path);
			try {
				IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), ifile);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		};

		tvc3.setLabelProvider(new LinkLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.getLocationString();
			}
		}, linkHandler));

		TableViewerColumn tvc4 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc4.getColumn().setText("ElapsedTime");
		tvc4.getColumn().setResizable(true);
		tvc4.getColumn().setMoveable(true);
		tvc4.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				MaudeResult mr = (MaudeResult) element;
				return mr.getElapsedTimeString();
			}
		});

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(1, true));
		layout.addColumnData(new ColumnWeightData(2, true));
		layout.addColumnData(new ColumnWeightData(1, true));

		viewer.getTable().setLayout(layout);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());

		workbench.getHelpSystem().setHelp(viewer.getControl(), "edu.postech.maude.view.viewer");
		getSite().setSelectionProvider(viewer);

		makeActions();
		hookDoubleClickAction();
		hookContextMenu();
		viewer.refresh();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(manager -> HybridSynchAADLView.this.fillContextMenu(manager));
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(stopProcessAction);
		manager.add(deleteResultAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		stopProcessAction = new Action() {
			@Override
			public void run() {
				MaudeResult mr = (MaudeResult) viewer.getStructuredSelection().getFirstElement();
				if (mr.checkProcess()) {
					mr.killProcess();
					mr.setResultString("Terminated");
					viewer.update(mr, null);
				}
			}
		};
		stopProcessAction.setText("Stop Process");
		stopProcessAction.setToolTipText("Action 1 tooltip");

		deleteResultAction = new Action() {
			@Override
			public void run() {
				MaudeResult mr = (MaudeResult) viewer.getStructuredSelection().getFirstElement();
				viewer.remove(mr);
			}
		};
		deleteResultAction.setText("Delete Result");
		deleteResultAction.setToolTipText("Action 1 tooltip");

		doubleClickAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				MaudeResult mr = (MaudeResult) obj;
				IPath path = mr.getLocationIPath();
				path = path.removeLastSegments(3).append("requirement");
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();

				Integer line = mr.findPropIdLine();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(IMarker.LINE_NUMBER, line);

				try {
					for (IResource resource : ((IContainer) root.findMember(path)).members()) {
						if (resource.getName().contains(mr.getPspcFileName())) {
							IMarker marker = ((IFile) resource).createMarker(IMarker.TEXT);
							marker.setAttributes(map);
							IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
									marker);
						}
					}
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
		};
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Sample View", message);
	}


	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(event -> doubleClickAction.run());
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public MaudeResult initialData(MaudeResult mr) {
		viewer.add(mr);
		return mr;
	}

	public void refreshData(EList<Property> propList) {
		for (TableItem ti : viewer.getTable().getItems()) {
			MaudeResult mr = (MaudeResult) ti.getData();
			boolean check = true;
			for (Property pr : propList) {
				if (mr.getPropId().equals(pr.getName())) {
					check = false;
				}
			}
			if (check) {
				viewer.remove(mr);
			}
		}
	}

	public void removeData(IFile prop) {
		for (TableItem ti : viewer.getTable().getItems()) {
			MaudeResult mr = (MaudeResult) ti.getData();
			if (mr.getPspcFileName().equals(prop.getName())) {
				viewer.remove(mr);
			}
		}
	}

	public void updateData(MaudeResult element) {
		viewer.add(element);
		System.out.println("Update Data");
	}

	public void updateData(MaudeResult oldElement, MaudeResult newElement) {
		viewer.add(newElement);
		viewer.remove(oldElement);
		System.out.println("refreshData method");

	}

	public void clear() {
		viewer.getTable().removeAll();
	}

}
