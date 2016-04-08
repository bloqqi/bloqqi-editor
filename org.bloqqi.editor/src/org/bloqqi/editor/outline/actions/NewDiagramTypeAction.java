package org.bloqqi.editor.outline.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.actions.MyWorkbenchPartAction;
import org.bloqqi.editor.commands.CreateDiagramTypeCommand;

public class NewDiagramTypeAction extends MyWorkbenchPartAction {
	public static final String ID = "org.bloqqi." + NewDiagramTypeAction.class.getSimpleName();

	private final boolean canUndo;
	private final String dialogMessage;
	private final String defaultName;
	private DiagramType newDiagramType;
	
	public NewDiagramTypeAction(BloqqiEditor editor) {
		this(editor, true, "Enter name", "");
	}

	public NewDiagramTypeAction(BloqqiEditor editor, boolean canUndo,
			String dialogMessage, String defaultName) {
		super(editor);
		this.canUndo = canUndo;
		this.dialogMessage = dialogMessage;
		this.defaultName = defaultName;
		setId(ID);
		setText("New diagram...");
		newDiagramType = null;
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {
		Shell shell = getWorkbenchPart().getSite().getShell();
		InputDialog dialog = new InputDialog(shell,
				"Create new diagram type", dialogMessage, defaultName, null);
		if(dialog.open() == InputDialog.OK) {
			Program p = getEditor().getProgram();
			String name = dialog.getValue().trim();
			if (Program.isIdValid(name)) {
				CreateDiagramTypeCommand cmd = new CreateDiagramTypeCommand(p, name, canUndo);
				if (cmd.canExecute()) {
					execute(cmd);
					newDiagramType = cmd.getNewDiagramType();
				} else {
					MessageDialog.openInformation(shell, "Error", "Type name " + name + " is already declared");
				}
			} else {
				MessageDialog.openInformation(shell, "Error", "Type name " + name + " is not valid");
			}
		}
	}
	
	public DiagramType getNewDiagramType() {
		return newDiagramType;
	}
}
