package org.bloqqi.editor.actions;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.InheritedBlock;
import org.bloqqi.compiler.ast.InlinedBlock;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.SetBlockInlineCommand;
import org.bloqqi.editor.editparts.BlockPart;


public class InlineAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + InlineAction.class.getSimpleName();
	private static final String INLINE_TEXT = "Inline";
	private static final String COLLAPSE_TEXT = "Collapse";
	
	public InlineAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText(INLINE_TEXT);
	}

	@Override
	protected boolean calculateEnabled() {
		BlockPart part = getSelected(BlockPart.class);
		if (part != null) {
			Block block = part.getModel();
			if (block.isInlined()) {
				setText(COLLAPSE_TEXT);
			} else {
				setText(INLINE_TEXT);
			}
			if (block.isInlined()) {
				return block.inlinedBlock().declaredInDiagramType() == block.diagramType();
			} else {
				return block.type().isDiagramType() && !block.isInherited();
			}
		}
		return false;
	}

	@Override 
	public void run() { 
		Block block = getSelected(BlockPart.class).getModel();
		Command cmd = null;
		if (block.isInlined()) {
			InlinedBlock inlinedBlock = (InlinedBlock) block;
			cmd = new SetBlockInlineCommand(
					getEditor().getCoordinates(),
					inlinedBlock.inlinedBlock(),
					false);
		} else if (!block.isInherited()) {
			InheritedBlock inhBlock = (InheritedBlock) block;
			cmd = new SetBlockInlineCommand(
					getEditor().getCoordinates(),
					inhBlock.getDeclaredBlock(),
					true);
		}
		execute(cmd);
	}
}
