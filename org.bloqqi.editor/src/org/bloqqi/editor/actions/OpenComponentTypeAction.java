	package org.bloqqi.editor.actions;

import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.editparts.ComponentPart;

public class OpenComponentTypeAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + OpenComponentTypeAction.class.getSimpleName();
	public static final String TEXT = "Open Declaration";
	
	public OpenComponentTypeAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText(TEXT);
	}

	@Override
	protected boolean calculateEnabled() {
		ComponentPart part = getSelected(ComponentPart.class);
		if (part != null) {
			setText(TEXT + " (" + part.getModel().type().name() + ")");
			return part.getModel().type().isDiagramType();
		}
		return false;
	}

	@Override
	public void run() {
		Component comp = getSelected(ComponentPart.class).getModel();
		DiagramType diagramType = ((DiagramType) comp.type());
		getEditor().showDiagramType(diagramType);
	}
}
