package org.bloqqi.editor.preferences;

import org.bloqqi.editor.Activator;
import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Bloqqi preferences");
	}
	
	@Override
	public void createFieldEditors() {
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.ASK_BLOCK_NAME,
				"Always ask for block name for new blocks",
				getFieldEditorParent()));
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.LAYOUT_OPERATIONS,
				"Show layout operations in context menu",
				getFieldEditorParent()));
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.ENABLE_EXPERIMENTAL_FEATURES,
				"Enable experimental features",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}