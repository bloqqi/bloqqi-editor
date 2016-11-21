package org.bloqqi.editor.commands;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.ComponentParameter;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.InheritedBlock;
import org.bloqqi.compiler.ast.InheritedConnection;
import org.bloqqi.compiler.ast.List;

public class DeleteBlockCommand extends Command {
	private final InheritedBlock inhBlock;
	private final Block declaredBlock;
	private final DiagramType diagramType;
	private final boolean canDelete;
	private final List<FlowDecl> oldFlowDecls;

	private int indexOfBlock;

	public DeleteBlockCommand(InheritedBlock inhBlock, DiagramType diagramType) {
		this.inhBlock = inhBlock;
		this.declaredBlock = inhBlock.getDeclaredBlock();
		this.canDelete = inhBlock.canDelete();
		this.diagramType = diagramType;
		this.oldFlowDecls = diagramType.getFlowDeclList().treeCopy();
	}
	
	@Override
	public boolean canExecute() {
		return canDelete;
	}

	// TODO:
	// Handle sub-classes as well
	@Override
	public void execute() {
		removeAffectedConnections();
		indexOfBlock = diagramType.getLocalBlockList().getIndexOfChild(declaredBlock);
		diagramType.getLocalBlockList().removeChild(indexOfBlock);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}

	private void removeAffectedConnections() {
		// Step 1: identify which connections to remove
		Set<FlowDecl> removeFlowDecls = new HashSet<>();
		for (ComponentParameter p: inhBlock.getInParameters()) {
			for (Connection c: p.ingoingConnections()) {
				removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
			}
		}
		for (ComponentParameter p: inhBlock.getOutParameters()) {
			for (Connection c: p.outgoingConnections()) {
				removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
			}
		}
		
		// Step 2: remove connections
		for (FlowDecl fd: removeFlowDecls) {
			diagramType.getFlowDeclList().removeChild(fd);
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		diagramType.setFlowDeclList(oldFlowDecls);
		diagramType.getLocalBlockList().insertChild(declaredBlock, indexOfBlock);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
}
