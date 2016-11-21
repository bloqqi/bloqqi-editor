package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Literal;
import org.bloqqi.compiler.ast.VarUse;

public class CreateLiteralCommand extends AbstractCreateConnectionCommand {
	private final Literal source;
	private final Anchor target;
	private final Connection connection;

	public CreateLiteralCommand(Literal source, Anchor target, Connection connection) {
		this.source = source;
		this.target = target;
		this.connection = connection;
	}
	
	@Override
	public boolean canExecute() {
		return source != null && target != null;
	}
	
	@Override
	public void execute() {
		DiagramType dt = target.diagramType();
		
		VarUse targetUse = access(target);
		if (targetUse == null) {
			couldExecute = false;
			return;
		}
		
		connection.setSource(source);
		connection.setTarget(targetUse);

		dt.addFlowDecl(connection);
		dt.program().flushAllAttributes();
		dt.notifyObservers();
		source.notifyObservers();
		target.notifyObservers();
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void undo() {
		DiagramType dt = target.diagramType();
		if (oldBlock != null) {
			int i = dt.getLocalBlockList().getIndexOfChild(newBlock);
			dt.getLocalBlockList().setChild(oldBlock, i);
		}
		dt.getFlowDeclList().removeChild(connection);
		dt.program().flushAllAttributes();
		dt.notifyObservers();
	}
}
