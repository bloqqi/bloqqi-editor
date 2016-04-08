package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.Literal;

public class DeleteLiteralCommand extends Command {
	private final DiagramType diagramType;
	private final boolean isInherited;
	private final FlowDecl declaredFlowDecl;
	private int flowDeclIndex;
	
	public DeleteLiteralCommand(Literal literal) {
		this.diagramType = literal.diagramType();
		this.isInherited = literal.enclosingConnection().isInherited();
		this.declaredFlowDecl = literal.enclosingConnection().declaredFlowDecl();
	}
	
	@Override
	public boolean canExecute() {
		return !isInherited;
	}
	
	@Override
	public void execute() {
		flowDeclIndex = diagramType.getFlowDecls().getIndexOfChild(declaredFlowDecl);
		diagramType.getFlowDecls().removeChild(declaredFlowDecl);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void undo() {
		diagramType.getFlowDecls().insertChild(declaredFlowDecl, flowDeclIndex);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
}
