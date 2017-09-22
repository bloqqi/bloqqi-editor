package org.bloqqi.editor.policies;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.editor.commands.DeleteParameterCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class ParameterComponentEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		DiagramType dt = (DiagramType) getHost().getParent().getModel();
		Parameter par = (Parameter) getHost().getModel();
		if (!par.isInherited()) {
			return new DeleteParameterCommand(par, dt);
		} else {
			return null;
		}
	}
}
