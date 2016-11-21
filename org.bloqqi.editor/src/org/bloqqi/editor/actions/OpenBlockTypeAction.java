	package org.bloqqi.editor.actions;

import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.editparts.BlockPart;

public class OpenBlockTypeAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + OpenBlockTypeAction.class.getSimpleName();
	public static final String TEXT = "Open Declaration";
	
	public OpenBlockTypeAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText(TEXT);
	}

	@Override
	protected boolean calculateEnabled() {
		BlockPart part = getSelected(BlockPart.class);
		if (part != null) {
			setText(TEXT + " (" + part.getModel().type().name() + ")");
			return part.getModel().type().isDiagramType();
		}
		return false;
	}

	@Override
	public void run() {
		Block block = getSelected(BlockPart.class).getModel();
		DiagramType diagramType = ((DiagramType) block.type());
		getEditor().showDiagramType(diagramType);
	}
}
