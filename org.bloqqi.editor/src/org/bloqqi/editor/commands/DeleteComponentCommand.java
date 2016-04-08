package org.bloqqi.editor.commands;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.ComponentParameter;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.InheritedComponent;
import org.bloqqi.compiler.ast.InheritedConnection;
import org.bloqqi.compiler.ast.List;

public class DeleteComponentCommand extends Command {
	private final InheritedComponent inhComponent;
	private final Component declaredComponent;
	private final DiagramType diagramType;
	private final boolean canDelete;
	private final List<FlowDecl> oldFlowDecls;

	private int indexOfComponent;

	public DeleteComponentCommand(InheritedComponent inhComponent, DiagramType diagramType) {
		this.inhComponent = inhComponent;
		this.declaredComponent = inhComponent.getDeclaredComponent();
		this.canDelete = inhComponent.canDelete();
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
		indexOfComponent = diagramType.getLocalComponentList().getIndexOfChild(declaredComponent);
		diagramType.getLocalComponentList().removeChild(indexOfComponent);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}

	private void removeAffectedConnections() {
		// Step 1: identify which connections to remove
		Set<FlowDecl> removeFlowDecls = new HashSet<>();
		for (ComponentParameter p: inhComponent.getInParameters()) {
			for (Connection c: p.ingoingConnections()) {
				removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
			}
		}
		for (ComponentParameter p: inhComponent.getOutParameters()) {
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
		diagramType.getLocalComponentList().insertChild(declaredComponent, indexOfComponent);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
}
