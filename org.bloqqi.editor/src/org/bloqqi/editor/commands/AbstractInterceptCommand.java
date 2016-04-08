package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;





import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Interception;

public abstract class AbstractInterceptCommand extends Command {
	protected Anchor source;
	protected Anchor target;
	protected Connection connection;
	protected Interception interception;
	
	public void setSource(Anchor source) {
		this.source = source;
	}
	
	public void setTarget(Anchor target) {
		this.target = target;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public boolean canExecute() {
		return source != null && target != null && connection != null
				&& connection.isInherited();
	}
	
	public void execute() {
		interception = createInterceptionDecl();
		
		DiagramType dt = source.diagramType();
		dt.addFlowDecl(interception);
		dt.program().flushAllAttributes();
		dt.notifyObservers();
		source.notifyObservers();
		target.notifyObservers();
	}

	public boolean canUndo() {
		return true;
	}
	
	public void undo() {
		DiagramType dt = source.diagramType();
		dt.getFlowDeclList().removeChild(interception);
		dt.program().flushAllAttributes();
		dt.notifyObservers();
	}
	
	protected abstract Interception createInterceptionDecl();
}
