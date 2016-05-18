package org.bloqqi.editor.tools;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

public abstract class AbstractDialogCreationTool
		<C extends Command, D extends Dialog>
		extends AbstractCreationTool {
	
	@Override
	protected void performCreation(int button) {
		Command cmd = getCurrentCommand();

		boolean performCreation = false;
		if (getCommandClass().isInstance(cmd)) {
			performCreation = createVariableDialog(cmd);
		}
		
		if (performCreation) {
			super.performCreation(button);
		} else {
			setCurrentCommand(null);
		}
	}

	protected boolean createVariableDialog(Command cmd) {
		boolean performCreation = true;

		C createCmd = getCommandClass().cast(cmd);
		D dialog = createDialog();;
		if (dialog.open() == Window.OK) {
			if (nameExists(createCmd, getName(dialog))) {
				String message = "Name \"" + getName(dialog) + "\" is already used.";
				MessageDialog.openError(properties.getEditor().getSite().getShell(),
						"Name already used",
						message);
				performCreation = false;
			} else {
				setValues(createCmd, dialog);
			}
		} else {
			performCreation = false;
		}
		
		return performCreation;
	}
	
	abstract protected Class<C> getCommandClass();
	abstract protected boolean nameExists(C cmd, String newName);
	abstract protected String getName(D dialog);
	abstract protected D createDialog();
	abstract protected void setValues(C cmd, D dialog);
}
