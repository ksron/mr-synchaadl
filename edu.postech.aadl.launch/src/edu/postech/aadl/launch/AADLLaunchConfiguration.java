package edu.postech.aadl.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public class AADLLaunchConfiguration extends LaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		System.out.println("Launch!");
		try {
			String maudePath = configuration.getAttribute(AADLLaunchConfigurationAttributes.MAUDE_PATH, "Maude_PATH_DEBUG");
			String pspcPath = configuration.getAttribute(AADLLaunchConfigurationAttributes.PSPC_PATH, "Pspc_PATH_DEBUG");
			String maudeOptions = configuration.getAttribute(AADLLaunchConfigurationAttributes.MAUDE_OPTION, "Maude_Options");

			System.out.println("maudePath : " + maudePath);
			System.out.println("maudeOptions : " + maudeOptions);
			System.out.println("pspcPath : " + pspcPath);

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
