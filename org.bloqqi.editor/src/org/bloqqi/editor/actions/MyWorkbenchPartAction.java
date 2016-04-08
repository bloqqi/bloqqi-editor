package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;

public abstract class MyWorkbenchPartAction extends WorkbenchPartAction {
	public MyWorkbenchPartAction(BloqqiEditor editor) {
		super(editor);
	}
	
	public BloqqiEditor getEditor() {
		return (BloqqiEditor) getWorkbenchPart();
	}
}
