package edu.postech.maude.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class MaudePrefPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public MaudePrefPage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, "edu.postech.maude.preferences.page"));
		setDescription("Maude Preferences");
	}

	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor("MAUDE_DIRECTORY", "MAUDE_DIRECTORY:", getFieldEditorParent()));
		addField(new FileFieldEditor("MAUDE", "MAUDE:", getFieldEditorParent()));
		addField(new StringFieldEditor("MAUDE_OPTIONS", "MAUDE_OPTIONS:", getFieldEditorParent()));
	}

}
