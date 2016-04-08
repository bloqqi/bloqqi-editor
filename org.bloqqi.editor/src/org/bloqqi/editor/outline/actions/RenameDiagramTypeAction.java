package org.bloqqi.editor.outline.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.actions.MySelectionAction;
import org.bloqqi.editor.commands.RenameDiagramTypeCommand;
import org.bloqqi.editor.outline.DiagramTypePartOutline;

public class RenameDiagramTypeAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + RenameDiagramTypeAction.class.getSimpleName();

	public RenameDiagramTypeAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Rename diagram...");
	}

	@Override
	protected boolean calculateEnabled() {
		if (getSelectedObjects().size() == 1
				&& getSelectedObjects().get(0) instanceof DiagramTypePartOutline) {
			return !getDiagramType().isAnonymousType();
		} else {
			return false;
		}
	}

	@Override
	public void run() {
		DiagramType dt = getDiagramType();

		Shell shell = getEditor().getSite().getShell();
		InputDialog dialog = new InputDialog(shell, "Rename diagram type", "Enter new name", dt.name(), null);
		if(dialog.open() == InputDialog.OK) {
			RenameDiagramTypeCommand cmd = new RenameDiagramTypeCommand(
					getEditor().getCoordinates(),
					dt,
					dialog.getValue());
			execute(cmd);
		}
	}
	
	private DiagramType getDiagramType() {
		return ((DiagramTypePartOutline) getSelectedObjects().get(0)).getModel();
	}
}
