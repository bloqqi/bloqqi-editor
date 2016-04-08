package org.bloqqi.editor.actions;

import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.editparts.ConnectionPart;
import org.bloqqi.editor.editparts.DiagramTypePart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;


public class ChangeAllConnectionRoutersAction extends MyWorkbenchPartAction {
	public static final String ID = "org.bloqqi." + ChangeAllConnectionRoutersAction.class.getSimpleName();

	public ChangeAllConnectionRoutersAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Change all connection routers");
		setToolTipText("Change all connection routers");
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override 
	public void run() {
		BloqqiEditor editor = (BloqqiEditor) getWorkbenchPart();
		RootEditPart root = editor.getRootEditPart();
		DiagramTypePart dtp = (DiagramTypePart) root.getContents();
		
		for (Object child: dtp.getChildren()) {
			EditPart component = (EditPart) child;
			for (Object child2: component.getChildren()) {
				AbstractGraphicalEditPart port = (AbstractGraphicalEditPart) child2;
				for (Object conn: port.getSourceConnections()) {
					ConnectionPart connectionPart = (ConnectionPart) conn;
					connectionPart.changeConnectionRouter();
				}
			}
		}
	}
}
