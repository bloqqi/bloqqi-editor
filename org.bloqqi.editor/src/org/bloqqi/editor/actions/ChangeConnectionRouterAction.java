package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.editparts.ConnectionPart;

public class ChangeConnectionRouterAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + ChangeConnectionRouterAction.class.getSimpleName();

	public ChangeConnectionRouterAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Change connection router");
		setToolTipText("Change connection router");
	}

	@Override
	protected boolean calculateEnabled() {
		return getSelected(ConnectionPart.class) != null;
	}
	
	@Override 
	public void run() {
		getSelected(ConnectionPart.class).changeConnectionRouter();
	}
}
