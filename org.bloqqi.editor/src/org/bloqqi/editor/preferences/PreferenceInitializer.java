package org.bloqqi.editor.preferences;

import org.bloqqi.editor.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.SHOW_TYPES, false);
		store.setDefault(PreferenceConstants.ASK_BLOCK_NAME, true);
		store.setDefault(PreferenceConstants.LAYOUT_OPERATIONS, false);
		store.setDefault(PreferenceConstants.ENABLE_EXPERIMENTAL_FEATURES, false);
	}
}
