package org.bloqqi.editor.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.InheritedComponent;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.RenameComponentCommand;
import org.bloqqi.editor.editparts.ComponentPart;

public class RenameComponentAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + RenameComponentAction.class.getSimpleName();

	public RenameComponentAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Rename...");
	}
	
	@Override
	protected boolean calculateEnabled() {
		ComponentPart part = getSelected(ComponentPart.class);
		if (part != null) {
			if (part.getModel() instanceof InheritedComponent) {
				return !((InheritedComponent) part.getModel()).isInherited();
			} else {
				return false;
			}
		}
		return false;
	}

	@Override 
	public void run() {
		ComponentPart editPart = getSelected(ComponentPart.class);
		
		Shell shell = getEditor().getSite().getShell();
		InputDialog dialog = new InputDialog(shell, "Rename", "Enter new name", editPart.getModel().name(), null);
		
		if(dialog.open() == InputDialog.OK) {
			RenameComponentCommand cmd = new RenameComponentCommand(
					getEditor().getCoordinates(),
					editPart.getModel(),
					dialog.getValue());
			execute(cmd);
		} 
	}
}
