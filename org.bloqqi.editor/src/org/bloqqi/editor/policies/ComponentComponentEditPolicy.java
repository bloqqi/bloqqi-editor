package org.bloqqi.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedComponent;
import org.bloqqi.editor.commands.DeleteComponentCommand;

public class ComponentComponentEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest request) { 
		DiagramType dt = (DiagramType)getHost().getParent().getModel();
		InheritedComponent inhComp = (InheritedComponent)getHost().getModel();
		return new DeleteComponentCommand(inhComp, dt);
	}
	
//	@Override
//	public void showSourceFeedback(Request request) {
//		if (request instanceof ChangeBoundsRequest) {
//			ChangeBoundsRequest c = (ChangeBoundsRequest) request;
//			System.out.println(c + ", " + c.getEditParts().get(0));
//		}
//	}
}
