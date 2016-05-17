package org.bloqqi.editor.commands;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.InheritedConnection;
import org.bloqqi.compiler.ast.InheritedVariable;
import org.bloqqi.compiler.ast.List;
import org.bloqqi.compiler.ast.Variable;

public class DeleteVariableCommand extends Command {
	private final InheritedVariable inhVar;
	private final Variable declaredVar;
	private final DiagramType diagramType;
	private final boolean canDelete;
	private final List<FlowDecl> oldFlowDecls;

	private int indexOfVar;

	public DeleteVariableCommand(InheritedVariable inhVar, DiagramType diagramType) {
		this.inhVar = inhVar;
		this.declaredVar = inhVar.getDeclaredVariable();
		this.canDelete = inhVar.canDelete();
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
		indexOfVar = diagramType.getLocalVariableList().getIndexOfChild(declaredVar);
		diagramType.getLocalVariableList().removeChild(indexOfVar);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}

	private void removeAffectedConnections() {
		// Step 1: identify which connections to remove
		Set<FlowDecl> removeFlowDecls = new HashSet<>();
		for (Connection c: inhVar.ingoingConnections()) {
			removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
		}
		for (Connection c: inhVar.outgoingConnections()) {
			removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
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
		diagramType.getLocalVariableList().insertChild(declaredVar, indexOfVar);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
}
