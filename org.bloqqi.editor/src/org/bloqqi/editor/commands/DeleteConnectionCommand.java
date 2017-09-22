package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.InheritedConnection;

public class DeleteConnectionCommand extends Command {
	private final boolean canDelete;
	private final FlowDecl declaredFlowDecl;
	private final DiagramType diagramType;
	private int declaredFlowDeclIndex;
	
	public DeleteConnectionCommand(InheritedConnection connection) {
		this.canDelete = connection.canDelete();
		this.declaredFlowDecl = connection.getDeclaredFlowDecl();
		this.diagramType = connection.diagramType();
	}
	
	@Override
	public boolean canExecute() {
		return canDelete;
	}

	@Override 
	public void execute() {
		declaredFlowDeclIndex = diagramType.getFlowDeclList().getIndexOfChild(declaredFlowDecl);
		diagramType.getFlowDeclList().removeChild(declaredFlowDecl);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
	
	
	@Override
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void undo() {
		diagramType.getFlowDeclList().insertChild(declaredFlowDecl, declaredFlowDeclIndex);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
}
