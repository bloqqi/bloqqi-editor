package org.bloqqi.editor.preferences;

import org.bloqqi.editor.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.ASK_COMPONENT_NAME, true);
		store.setDefault(PreferenceConstants.LAYOUT_OPERATIONS, false);
	}
}
