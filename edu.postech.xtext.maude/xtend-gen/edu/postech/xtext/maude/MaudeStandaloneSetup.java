/**
 * generated by Xtext 2.17.0
 */
package edu.postech.xtext.maude;

import edu.postech.xtext.maude.MaudeStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
@SuppressWarnings("all")
public class MaudeStandaloneSetup extends MaudeStandaloneSetupGenerated {
  public static void doSetup() {
    new MaudeStandaloneSetup().createInjectorAndDoEMFRegistration();
  }
}
