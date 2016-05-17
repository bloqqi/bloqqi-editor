package org.bloqqi.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedVariable;
import org.bloqqi.editor.commands.DeleteVariableCommand;

public class VariableComponentEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		DiagramType dt = (DiagramType) getHost().getParent().getModel();
		InheritedVariable inhVar = (InheritedVariable) getHost().getModel();
		return new DeleteVariableCommand(inhVar, dt);
	}
}
