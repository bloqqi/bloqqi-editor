package org.bloqqi.editor.policies;

import org.eclipse.gef.commands.Command;

import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedBlock;
import org.bloqqi.editor.commands.DeleteBlockCommand;

public class BlockComponentEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest request) { 
		DiagramType dt = (DiagramType)getHost().getParent().getModel();
		InheritedBlock inhBlock = (InheritedBlock)getHost().getModel();
		return new DeleteBlockCommand(inhBlock, dt);
	}
	
//	@Override
//	public void showSourceFeedback(Request request) {
//		if (request instanceof ChangeBoundsRequest) {
//			ChangeBoundsRequest c = (ChangeBoundsRequest) request;
//			System.out.println(c + ", " + c.getEditParts().get(0));
//		}
//	}
}
