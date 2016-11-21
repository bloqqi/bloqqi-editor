package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.VarUse;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {
	private Anchor source;
	private Anchor target;

	@Override
	public boolean canExecute() {
		return source != null && target != null;
	}
	
	@Override
	public void execute() {
		DiagramType dt = source.diagramType();
		
		if (!source.canAccess() && !target.canAccess()) {
			couldExecute = false;
			return;
		}
		
		VarUse sourceUse = access(source);
		VarUse targetUse = access(target);
		if (sourceUse == null || targetUse == null) {
			couldExecute = false;
			return;
		}
		connection.setSource(sourceUse);
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
		DiagramType dt = source.diagramType();
		if (oldBlock != null) {
			int i = dt.getLocalBlockList().getIndexOfChild(newBlock);
			dt.getLocalBlockList().setChild(oldBlock, i);
		}
		dt.getFlowDeclList().removeChild(connection);
		dt.program().flushAllAttributes();
		dt.notifyObservers();
	}

	public void setSource(Anchor source) {
		this.source = source;
	}
	
	public Anchor getSource() {
		return source;
	}
	
	public void setTarget(Anchor target) {
		this.target = target;
	}
	
	public Anchor getTarget() {
		return target;
	}
}
