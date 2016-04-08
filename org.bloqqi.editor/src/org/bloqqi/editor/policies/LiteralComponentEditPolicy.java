package org.bloqqi.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.bloqqi.compiler.ast.Literal;
import org.bloqqi.editor.commands.DeleteLiteralCommand;

public class LiteralComponentEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		Literal literal = (Literal) getHost().getModel();
		return new DeleteLiteralCommand(literal);
	}
}
