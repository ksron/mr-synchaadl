package edu.postech.maude.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class MaudePrefInitializer extends AbstractPreferenceInitializer {

	public MaudePrefInitializer() {
		System.out.println("Called");
	}

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				"edu.postech.maude.preferences.page");
		scopedPreferenceStore.setDefault("MAUDEOPTIONS", "-no-prelude prelude.maude smt.maude");
	}
}
