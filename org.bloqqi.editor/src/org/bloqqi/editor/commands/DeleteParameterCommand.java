package org.bloqqi.editor.commands;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.InheritedConnection;
import org.bloqqi.compiler.ast.List;
import org.bloqqi.compiler.ast.Parameter;

public class DeleteParameterCommand extends Command {
	private final Parameter inhPar;
	private final Parameter declaredPar;
	private final DiagramType diagramType;
	private final boolean canDelete;
	private List<FlowDecl> oldFlowDecls;

	private int indexOfVar;

	public DeleteParameterCommand(Parameter inhPar, DiagramType diagramType) {
		this.inhPar = inhPar;
		this.declaredPar = inhPar.declaredParameter();
		this.canDelete = inhPar.canDelete();
		this.diagramType = diagramType;
	}
	
	@Override
	public boolean canExecute() {
		return canDelete;
	}

	// TODO:
	// Handle sub-classes as well
	@Override
	public void execute() {
		oldFlowDecls = diagramType.getFlowDeclList().treeCopy();
		
		removeAffectedConnections();
		
		if (inhPar.isInParameter()) {
			indexOfVar = diagramType.getLocalInParameterList().getIndexOfChild(declaredPar);
			diagramType.getLocalInParameterList().removeChild(indexOfVar);
		} else {
			indexOfVar = diagramType.getLocalOutParameterList().getIndexOfChild(declaredPar);
			diagramType.getLocalOutParameterList().removeChild(indexOfVar);
		}
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}

	private void removeAffectedConnections() {
		// Step 1: identify which connections to remove
		Set<FlowDecl> removeFlowDecls = new HashSet<>();
		for (Connection c: inhPar.ingoingConnections()) {
			removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
		}
		for (Connection c: inhPar.outgoingConnections()) {
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
		if (inhPar.isInParameter()) {
			diagramType.getLocalInParameterList().insertChild(declaredPar, indexOfVar);
		} else {
			diagramType.getLocalOutParameterList().insertChild(declaredPar, indexOfVar);
		}
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
}
