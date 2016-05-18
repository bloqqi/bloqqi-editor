package org.bloqqi.editor.tools;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.bloqqi.editor.commands.CreateVariableCommand;
import org.bloqqi.editor.wizards.AddVariableDialog;

public class VariableCreationTool extends AbstractCreationTool {
	@Override
	protected void performCreation(int button) {
		Command cmd = getCurrentCommand();

		boolean performCreation = false;
		if (cmd instanceof CreateVariableCommand) {
			performCreation = createVariableDialog(cmd);
		}
		
		if (performCreation) {
			super.performCreation(button);
		} else {
			setCurrentCommand(null);
		}
	}

	private boolean createVariableDialog(Command cmd) {
		boolean performCreation = true;
		CreateVariableCommand createCmd = (CreateVariableCommand) cmd;
		
		AddVariableDialog addDialog = new AddVariableDialog(properties.getEditor().getSite().getShell());
		if (addDialog.open() == Window.OK) {
			if (createCmd.nameExists(addDialog.getName())) {
				String message = "Variable \"" + addDialog.getName()
					+ "\" cannot be added: name is already used.";
				MessageDialog.openError(properties.getEditor().getSite().getShell(),
						"Name already used",
						message);
				performCreation = false;
			} else {
				createCmd.setName(addDialog.getName());
				createCmd.setType(addDialog.getType());
			}
		} else {
			performCreation = false;
		}
		
		return performCreation;
	}
}
