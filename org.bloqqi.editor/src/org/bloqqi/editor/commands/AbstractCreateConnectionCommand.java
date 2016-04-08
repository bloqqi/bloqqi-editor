package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.ComponentParameter;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.VarUse;
import org.bloqqi.editor.PostConditionCommand;

public abstract class AbstractCreateConnectionCommand extends PostConditionCommand {
	protected Connection connection;
	protected Component oldComponent;
	protected Component newComponent;

	protected boolean couldExecute;

	public AbstractCreateConnectionCommand() {
		oldComponent = null;
		couldExecute = true;
	}
	
	protected VarUse access(Anchor anchor) {
		if (anchor.canAccess()) {
			return anchor.access();
		} else if (anchor.canModifyToAccess()) {
			if (oldComponent != null) {
				return null;
			}
			oldComponent = ((ComponentParameter) anchor).component().inlinedComponent();
			Pair<Component, VarUse> p = anchor.modifyToAccess();
			newComponent = p.first;
			return p.second;
		} else {
			throw new RuntimeException("Connection could not be created.");
		}
	}
	
	@Override
	public boolean couldExecute() {
		return couldExecute;
	}

	@Override
	public String getErrorMessage() {
		return "Connection could not be created";
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public Connection getConnection() {
		return connection;
	}
}
