package org.bloqqi.editor.tools;

import org.bloqqi.editor.commands.CreateVariableCommand;
import org.bloqqi.editor.wizards.AddVariableDialog;

public class VariableCreationTool
		extends AbstractDialogCreationTool<CreateVariableCommand, AddVariableDialog> {

	public VariableCreationTool() {
		super(CreateVariableCommand.class);
	}

	@Override
	protected boolean nameExists(CreateVariableCommand cmd, String newName) {
		return cmd.nameExists(newName);
	}

	@Override
	protected AddVariableDialog createDialog() {
		return new AddVariableDialog(properties.getEditor().getSite().getShell());
	}

	@Override
	protected String getName(AddVariableDialog dialog) {
		return dialog.getName();
	}

	@Override
	protected void setValues(CreateVariableCommand cmd, AddVariableDialog dialog) {
		cmd.setName(dialog.getName());
		cmd.setType(dialog.getType());
	}
}
