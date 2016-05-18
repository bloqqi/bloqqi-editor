package org.bloqqi.editor.tools;

import org.bloqqi.editor.commands.CreateParameterCommand;
import org.bloqqi.editor.wizards.AddParameterDialog;

public class ParameterCreationTool
		extends AbstractDialogCreationTool<CreateParameterCommand, AddParameterDialog> {

	@Override
	protected Class<CreateParameterCommand> getCommandClass() {
		return CreateParameterCommand.class;
	}

	@Override
	protected boolean nameExists(CreateParameterCommand cmd, String newName) {
		return cmd.nameExists(newName);
	}

	@Override
	protected AddParameterDialog createDialog() {
		return new AddParameterDialog(properties.getEditor().getSite().getShell());
	}

	@Override
	protected String getName(AddParameterDialog dialog) {
		return dialog.getName();
	}

	@Override
	protected void setValues(CreateParameterCommand cmd, AddParameterDialog dialog) {
		cmd.setName(dialog.getName());
		cmd.setType(dialog.getType());
		cmd.setIsInParameter(dialog.isInParameter());
	}
}
