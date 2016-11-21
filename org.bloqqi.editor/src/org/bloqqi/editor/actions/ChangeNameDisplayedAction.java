package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.Properties;


public class ChangeNameDisplayedAction extends MyWorkbenchPartAction {
	public static final String ID = "org.bloqqi." + ChangeNameDisplayedAction.class.getSimpleName();

	public ChangeNameDisplayedAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Change name displayed");
	}
	
	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override 
	public void run() {
		int before = Properties.instance().getInt(Properties.KEY_BLOCK_NAME, 0);
		Properties.instance().put(Properties.KEY_BLOCK_NAME, ++before%3);
		getEditor().refresh();
	}
}
