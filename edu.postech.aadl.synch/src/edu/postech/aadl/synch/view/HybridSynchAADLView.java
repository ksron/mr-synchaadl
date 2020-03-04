package edu.postech.aadl.synch.view;


import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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

import edu.postech.aadl.maude.Maude;
import edu.postech.aadl.utils.IOUtils;

public class HybridSynchAADLView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.postech.aadl.view.HybridSynchAADLView";

	@Inject
	public IWorkbench workbench;

	private TableViewer viewer;
	private Action doubleClickAction;
	private Action stopProcessAction;
	private Action deleteResultAction;
	private Action makeCSV;

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
				Maude mr = (Maude) element;
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
				Maude mr = (Maude) element;
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
				Maude mr = (Maude) element;
				return mr.getResultString();
			}
		});

		TableViewerColumn tvc3 = new TableViewerColumn(viewer, SWT.LEFT);
		tvc3.getColumn().setText("Location");
		tvc3.getColumn().setResizable(true);
		tvc3.getColumn().setMoveable(true);

		LinkOpener linkHandler = rowObject -> {
			Maude mr = (Maude)rowObject;
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
				Maude mr = (Maude) element;
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
				Maude mr = (Maude) element;
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
		manager.add(makeCSV);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		stopProcessAction = new Action() {
			@Override
			public void run() {
				Maude mr = (Maude) viewer.getStructuredSelection().getFirstElement();
				if (mr.checkProcess()) {
					mr.killProcess();
					mr.setResultString("Terminated");
					viewer.update(mr, null);
				}
			}
		};
		stopProcessAction.setText("Stop Process");

		deleteResultAction = new Action() {
			@Override
			public void run() {
				Maude mr = (Maude) viewer.getStructuredSelection().getFirstElement();
				viewer.remove(mr);
			}
		};
		deleteResultAction.setText("Delete Result");

		makeCSV = new Action() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("PSPC File,Property Id,Result,Location,ElapsedTime\n");
				for (TableItem ti : viewer.getTable().getItems()) {
					Maude maude = (Maude) ti.getData();
					sb.append(maude.getPspcFileName() + "," + maude.getPropId() + "," + maude.getResultString() + ","
							+ maude.getLocationString() + "," + maude.getElapsedTimeString() + "\n");
				}
				Maude mr = (Maude) viewer.getStructuredSelection().getFirstElement();
				String time = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new Date());
				IPath csvPath = mr.getLocationIPath().removeLastSegments(2).append("csv")
						.append("result_" + time + ".csv");
				try {
					IOUtils.setFileContent(new ByteArrayInputStream(sb.toString().getBytes()),
							IOUtils.getFile(csvPath));
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		};
		makeCSV.setText("Export all data into CSV file");

		doubleClickAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Maude mr = (Maude) selection.getFirstElement();
				Integer line = mr.findPropIdLine();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(IMarker.LINE_NUMBER, line);
				try {
					IMarker marker = mr.getPSPCFile().createMarker(IMarker.TEXT);
					marker.setAttributes(map);
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), marker);
				} catch (CoreException e1) {
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

	public Maude initialData(Maude mr) {
		viewer.add(mr);
		return mr;
	}

	public void removeData(IFile prop) {
		for (TableItem ti : viewer.getTable().getItems()) {
			Maude mr = (Maude) ti.getData();
			if (mr.getPspcFileName().equals(prop.getName())) {
				viewer.remove(mr);
			}
		}
	}

	public void updateData(Maude oldElement, Maude newElement) {
		viewer.remove(oldElement);
		viewer.add(newElement);
	}
}
