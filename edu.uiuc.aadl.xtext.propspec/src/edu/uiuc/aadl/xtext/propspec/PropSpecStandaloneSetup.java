
package edu.uiuc.aadl.xtext.propspec;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class PropSpecStandaloneSetup extends PropSpecStandaloneSetupGenerated{

	public static void doSetup() {
		new PropSpecStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

