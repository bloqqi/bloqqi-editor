package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.AutoLayoutCommand;
import org.eclipse.gef.commands.Command;

public class AutoLayoutAction extends MyWorkbenchPartAction {
	public static final String ID = "org.bloqqi." + AutoLayoutAction.class.getSimpleName();
	
	public AutoLayoutAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Auto layout");
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override 
	public void run() {
		Command cmd = new AutoLayoutCommand(
				getEditor().getDiagramType(),
				getEditor().getCoordinates());
		execute(cmd);
	}
}
