package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

public abstract class MyWorkbenchPartAction extends WorkbenchPartAction {
	public MyWorkbenchPartAction(BloqqiEditor editor) {
		super(editor);
	}
	
	public BloqqiEditor getEditor() {
		return (BloqqiEditor) getWorkbenchPart();
	}

	protected String getInput(String title, String message, String value) {
		Shell shell = getEditor().getSite().getShell();
		InputDialog dialog = new InputDialog(shell, title, message, value, null);
		
		if(dialog.open() == InputDialog.OK) {
			return dialog.getValue();
		} else {
			return null;
		}
	}
}
