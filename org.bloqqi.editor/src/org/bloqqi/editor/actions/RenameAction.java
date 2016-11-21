package org.bloqqi.editor.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.RenameNodeCommand;
import org.bloqqi.editor.commands.RenameBlockCommand;
import org.bloqqi.editor.commands.RenameParameterCommand;
import org.bloqqi.editor.commands.RenameVariableCommand;
import org.bloqqi.editor.editparts.AbstractNodePart;
import org.bloqqi.editor.editparts.BlockPart;
import org.bloqqi.editor.editparts.ParameterPart;
import org.bloqqi.editor.editparts.VariablePart;

public class RenameAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + RenameAction.class.getSimpleName();

	public RenameAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Rename...");
	}
	
	@Override
	protected boolean calculateEnabled() {
		AbstractNodePart<?> part = getSelected(AbstractNodePart.class);
		if (part != null) {
			return !part.getModel().isInherited();
		}
		return false;
	}

	@Override
	public void run() {
		AbstractNodePart<?> editPart = getSelected(AbstractNodePart.class);
		
		Shell shell = getEditor().getSite().getShell();
		InputDialog dialog = new InputDialog(shell, "Rename",
				"Enter new name", editPart.getModel().name(), null);
		
		if(dialog.open() == InputDialog.OK) {
			RenameNodeCommand cmd = null;
			if (editPart instanceof BlockPart) {
				cmd = new RenameBlockCommand(((BlockPart) editPart).getModel());
			} else if (editPart instanceof ParameterPart) {
				cmd = new RenameParameterCommand(((ParameterPart<?>) editPart).getModel());
			} else if (editPart instanceof VariablePart) {
				cmd = new RenameVariableCommand(((VariablePart) editPart).getModel());
			}
			if (cmd != null) {
				cmd.setCoordinates(getEditor().getCoordinates());
				cmd.setNewName(dialog.getValue());
				execute(cmd);
			}
		}
	}
}
