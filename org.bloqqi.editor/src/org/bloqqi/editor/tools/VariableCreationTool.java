package org.bloqqi.editor.tools;

import org.bloqqi.compiler.ast.InputVariable;
import org.bloqqi.compiler.ast.OutputVariable;
import org.bloqqi.compiler.ast.StateVariable;
import org.bloqqi.editor.commands.CreateVariableCommand;
import org.bloqqi.editor.wizards.AddVariableDialog;

public class VariableCreationTool
		extends AbstractDialogCreationTool<CreateVariableCommand, AddVariableDialog> {
	
	@Override
	protected Class<CreateVariableCommand> getCommandClass() {
		return CreateVariableCommand.class;
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

		switch (dialog.getKind()) {
		case AddVariableDialog.KIND_STATE:
			cmd.setVariable(new StateVariable());
			break;
		case AddVariableDialog.KIND_INPUT:
			cmd.setVariable(new InputVariable());
			break;
		case AddVariableDialog.KIND_OUTPUT:
			cmd.setVariable(new OutputVariable());
			break;
		}
	}
}
