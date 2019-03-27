package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.editparts.ConnectionPart;
import org.bloqqi.editor.tools.InterceptTool;
import org.bloqqi.editor.tools.CreateInterceptRequest.InterceptKind;


public class SourceInterceptAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + SourceInterceptAction.class.getSimpleName();
	
	public SourceInterceptAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Source port (advanced)");
	}
	
	@Override
	protected boolean calculateEnabled() {
		ConnectionPart cp = getSelected(ConnectionPart.class);
		return cp != null && cp.getModel().isInherited() && !cp.getModel().isInlined();
	}

	@Override 
	public void run() {
		ConnectionPart cp = getSelected(ConnectionPart.class);
		getEditor().setActiveToolTool(new InterceptTool(cp, InterceptKind.SOURCE));
	}
}

