package org.bloqqi.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;
import org.bloqqi.compiler.ast.InheritedConnection;
import org.bloqqi.editor.commands.DeleteConnectionCommand;

public class ConnectionEditPolicy extends org.eclipse.gef.editpolicies.ConnectionEditPolicy {
	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		return new DeleteConnectionCommand((InheritedConnection) getHost().getModel());
	}
}
