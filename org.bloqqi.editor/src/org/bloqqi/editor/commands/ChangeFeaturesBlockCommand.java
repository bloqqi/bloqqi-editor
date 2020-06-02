package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;



import java.util.HashMap;
import java.util.Map;

import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FeatureSelection;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.Pair;

public class ChangeFeaturesBlockCommand extends Command {
	private final Block oldBlock;
	private final int blockIndex;
	private final FeatureSelection selection;
	private final DiagramType enclosingDt;

	private Block newBlock;

	private Map<FlowDecl, Integer> removeFlowDecls;

	public ChangeFeaturesBlockCommand(Block existingBlock,
			FeatureSelection selection) {
		this.oldBlock = existingBlock;
		this.selection = selection;
		this.enclosingDt = existingBlock.diagramType();
		this.blockIndex = enclosingDt.getLocalBlocks().getIndexOfChild(existingBlock);
	}

	public void execute() {
		this.newBlock = oldBlock.treeCopy();
		this.newBlock.setType(selection.newAnonymousBlock("").getType());

		enclosingDt.getLocalBlockList().setChild(newBlock, blockIndex);
		enclosingDt.program().flushAllAttributes();

		removeAffectedConnections();
		enclosingDt.program().flushAllAttributes();

		enclosingDt.notifyObservers();
	}

	private void removeAffectedConnections() {
		removeFlowDecls = new HashMap<>();
		for (FlowDecl fd: enclosingDt.getFlowDecls()) {
			for (Pair<Node, Anchor> p: fd.connectedNodesAnchors()) {
				if (p.first == newBlock && p.second == null) {
					removeFlowDecls.put(fd, enclosingDt.getFlowDecls().getIndexOfChild(fd));
					break;
				}
			}
		}

		for (FlowDecl fd: removeFlowDecls.keySet()) {
			enclosingDt.getFlowDecls().removeChild(fd);
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	public void undo() {
		enclosingDt.getLocalBlockList().setChild(oldBlock, blockIndex);
		for (Map.Entry<FlowDecl, Integer> e: removeFlowDecls.entrySet()) {
			enclosingDt.getFlowDecls().insertChild(e.getKey(), e.getValue());
		}
		enclosingDt.program().flushAllAttributes();
		enclosingDt.notifyObservers();
	}
}
