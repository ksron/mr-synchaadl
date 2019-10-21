package edu.postech.aadl.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;

public class AADLLaunchShortcut implements ILaunchShortcut, ILaunchShortcut2 {

	@Override
	public void launch(ISelection selection, String mode) {
		launch(getLaunchableResource(selection), mode);
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		launch(getLaunchableResource(editor), mode);
	}

	@Override
	public final IResource getLaunchableResource(final IEditorPart editorpart) {
		final IEditorInput input = editorpart.getEditorInput();
		if (input instanceof XtextEditor) {
			return ((XtextEditor) input).getResource();
		}
		return null;
	}

	@Override
	public IResource getLaunchableResource(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			for (final Object element : ((IStructuredSelection) selection).toArray()) {
				if (element instanceof IFile) {
					return (IResource) element;
				}
			}
		}
		return null;
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		return getLaunchConfgurations(getLaunchableResource(selection));
	}

	@Override
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editorpart) {
		return getLaunchConfgurations(getLaunchableResource(editorpart));
	}

	public static IFile getPspcFile(final ILaunchConfiguration configuration) throws CoreException {
		String pspcPath = configuration.getAttribute(AADLLaunchConfigurationAttributes.PSPC_PATH, "");
		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(pspcPath));
	}

	private ILaunchConfiguration[] getLaunchConfgurations(IResource resource) {
		List<ILaunchConfiguration> configurations = new ArrayList<ILaunchConfiguration>();

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(AADLLaunchConfigurationAttributes.LAUNCH_CONFIGURATION_TYPE_ID);

		// try to find existing configurations using the same file
		try {
			for (ILaunchConfiguration configuration : manager.getLaunchConfigurations(type)) {
				try {
					IFile file = getPspcFile(configuration);
					if (resource.equals(file)) {
						configurations.add(configuration);
					}
				} catch (CoreException e) {
					// could not read configuration, ignore
				}
			}
		} catch (CoreException e) {
			// could not load configurations, ignore
		}

		return configurations.toArray(new ILaunchConfiguration[configurations.size()]);
	}

	private void launch(final IResource file, final String mode) {

		if (file instanceof IFile) {
			// try to save dirty editors
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(true);

			try {
				ILaunchConfiguration[] configurations = getLaunchConfgurations(file);
				if (configurations.length == 0) {
					// no configuration found, create new one
					ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
					ILaunchConfigurationType type = manager
							.getLaunchConfigurationType(AADLLaunchConfigurationAttributes.LAUNCH_CONFIGURATION_TYPE_ID);

					ILaunchConfigurationWorkingCopy configuration = type.newInstance(null, file.getName());
					configuration.setAttribute(AADLLaunchConfigurationAttributes.PSPC_PATH,
							file.getProjectRelativePath().toPortableString());

					// save and return new configuration
					configuration.doSave();

					configurations = new ILaunchConfiguration[] { configuration };
				}

				// launch
				configurations[0].launch(mode, new NullProgressMonitor());

			} catch (CoreException e) {
				// could not create launch configuration, run file directly
				// launch((IFile) file, null, mode, null, new NullProgressMonitor());
			}
		}
	}

}