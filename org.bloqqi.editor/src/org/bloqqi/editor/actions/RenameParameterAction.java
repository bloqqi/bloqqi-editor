package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.RenameParameterCommand;
import org.bloqqi.editor.editparts.ParameterPart;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;


public class RenameParameterAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + RenameParameterAction.class.getSimpleName();

	public RenameParameterAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Rename...");
	}

	@Override
	protected boolean calculateEnabled() {
		ParameterPart<?> part = getSelected(ParameterPart.class);
		if (part != null) {
			return !part.getModel().isInherited();
		}
		return false;
	}

	@Override
	public void run() {
		ParameterPart<?> part = getSelected(ParameterPart.class);
		
		Shell shell = getEditor().getSite().getShell();
		InputDialog dialog = new InputDialog(shell, "Rename", "Enter new name", part.getModel().name(), null);
		
		if(dialog.open() == InputDialog.OK) {
			RenameParameterCommand cmd = new RenameParameterCommand(
				getEditor().getCoordinates(),
				part.getModel(),
				dialog.getValue());
			execute(cmd);
		}
	}
}
