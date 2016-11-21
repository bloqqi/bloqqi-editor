package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;



import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FeatureConfiguration;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.VarUse;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class ChangeBlockSpecializationCommand extends Command {
	private final Block oldBlock;
	private final int blockIndex;
	private final FeatureConfiguration conf;
	private final DiagramType enclosingDt;
	private final Set<NewInParameter> newInParameters;

	private Block newBlock;
	
	private boolean hasExecuted;
	private Map<FlowDecl, Integer> removeFlowDecls;
	
	public ChangeBlockSpecializationCommand(Block existingBlock,
			FeatureConfiguration conf,
			Set<NewInParameter> newInParameters) {
		this.oldBlock = existingBlock;
		this.conf = conf;
		this.enclosingDt = existingBlock.diagramType();
		this.blockIndex = enclosingDt.getLocalBlocks().getIndexOfChild(existingBlock);
		this.newInParameters = newInParameters;
		hasExecuted = false;
	}
	
	public void execute() {
		this.newBlock = oldBlock.treeCopy();
		this.newBlock.setType(conf.newAnonymousBlock("").getType());

		enclosingDt.getLocalBlockList().setChild(newBlock, blockIndex);
		enclosingDt.program().flushAllAttributes();
		
		if (!hasExecuted) {
			// Some parameters of nested blocks should be exposed as parameters.
			// This is set by the user in the specialization wizard.
			for (NewInParameter in: newInParameters) {
				String parameter = oldBlock.name() + "." + in.getPath();
				Pair<Block, VarUse> p = enclosingDt.addConnectionsParameters(parameter, in.getNewName());
				newBlock = p.first;
			}
		}
		enclosingDt.program().flushAllAttributes();
		
		removeAffectedConnections();
		enclosingDt.program().flushAllAttributes();

		enclosingDt.notifyObservers();
		
		hasExecuted = true;
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
