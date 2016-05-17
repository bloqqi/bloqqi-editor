package org.bloqqi.editor.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.RenameNodeCommand;
import org.bloqqi.editor.commands.RenameComponentCommand;
import org.bloqqi.editor.commands.RenameParameterCommand;
import org.bloqqi.editor.editparts.AbstractNodePart;
import org.bloqqi.editor.editparts.ComponentPart;
import org.bloqqi.editor.editparts.ParameterPart;

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
			if (editPart instanceof ComponentPart) {
				cmd = new RenameComponentCommand(((ComponentPart) editPart).getModel());
			} else if (editPart instanceof ParameterPart) {
				cmd = new RenameParameterCommand(((ParameterPart<?>) editPart).getModel());
			}
			if (cmd != null) {
				cmd.setCoordinates(getEditor().getCoordinates());
				cmd.setNewName(dialog.getValue());
				execute(cmd);
			}
		}
	}
}
