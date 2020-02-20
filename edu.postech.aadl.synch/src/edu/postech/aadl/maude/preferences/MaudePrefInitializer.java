package edu.postech.aadl.maude.preferences;

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
		scopedPreferenceStore.setDefault(MaudePrefPage.MAUDE_OPTS, "-no-prelude");
		scopedPreferenceStore.setDefault(MaudePrefPage.MAUDE, "maude.darwin64");
		scopedPreferenceStore.setDefault(MaudePrefPage.MAUDE_DIR, "maude");
		scopedPreferenceStore.setDefault(MaudePrefPage.MAUDE_LIB_DIR, "maude");
	}
}
