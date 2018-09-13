package org.bloqqi.editor.actions;

import org.bloqqi.editor.Activator;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.preferences.PreferenceConstants;

public class ShowTypesAction extends MyWorkbenchPartAction {
	public static final String ID = "org.bloqqi." + ShowTypesAction.class.getSimpleName();

	public ShowTypesAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Show/hide types");
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {
		boolean showTypes = Activator.isPreferenceSet(PreferenceConstants.SHOW_TYPES);
		Activator.setPreference(PreferenceConstants.SHOW_TYPES, !showTypes);
		getEditor().getDiagramType().notifyObservers();
	}
}
