package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedBlock;

public class RenameBlockCommand extends RenameNodeCommand {
	private final Block declaredBlock;

	public RenameBlockCommand(Block block) {
		super(block.declaredBlock(),
				block.name(),
				block.diagramType(),
				block.isInherited());
		this.declaredBlock = block.declaredBlock();
	}
	
	protected String getNewAccessString(DiagramType forDiagramType) {
		InheritedBlock inhBlock = null;
		for (InheritedBlock ib: forDiagramType.getBlocks()) {
			if (ib.getDeclaredBlock() == declaredBlock) {
				inhBlock = ib;
			}
		}
		return inhBlock.accessString();
	}
}
