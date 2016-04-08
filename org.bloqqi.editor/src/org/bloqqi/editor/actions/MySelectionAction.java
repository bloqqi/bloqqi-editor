package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.eclipse.gef.ui.actions.SelectionAction;

public abstract class MySelectionAction extends SelectionAction {
	public MySelectionAction(BloqqiEditor editor) {
		super(editor);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getSelected(Class<T> clazz) {
		if (getSelectedObjects().size() == 1
				&& clazz.isInstance(getSelectedObjects().get(0))) {
			return (T) getSelectedObjects().get(0);
		}
		return null;
	}
	
	public BloqqiEditor getEditor() {
		return (BloqqiEditor) getWorkbenchPart();
	}
}
