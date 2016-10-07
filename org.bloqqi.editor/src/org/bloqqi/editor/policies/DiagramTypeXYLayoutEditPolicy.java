package org.bloqqi.editor.policies;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.Variable;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.ChangeConstraintComponentCommand;
import org.bloqqi.editor.commands.ChangeConstraintNodeCommand;
import org.bloqqi.editor.commands.CreateComponentCommand;
import org.bloqqi.editor.commands.CreateParameterCommand;
import org.bloqqi.editor.commands.CreateVariableCommand;
import org.bloqqi.editor.editparts.ComponentPart;

public class DiagramTypeXYLayoutEditPolicy extends XYLayoutEditPolicy {
	private final BloqqiEditor editor;

	public DiagramTypeXYLayoutEditPolicy(BloqqiEditor editor) {
		this.editor = editor;
	}
	
	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		ChangeConstraintNodeCommand command;
		if (child instanceof ComponentPart) {
			command = new ChangeConstraintComponentCommand();
		} else {
			command = new ChangeConstraintNodeCommand();
		}
		command.setNode((Node) child.getModel());
		command.setNewConstraint((Rectangle) constraint);
		command.setCoordinates(editor.getCoordinates());
		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObjectType().equals(Component.class)) {
			return new CreateComponentCommand(
					getLocation(request),
					(Component) request.getNewObject(),
					(DiagramType) getHost().getModel(),
					editor.getCoordinates());
		} else if (request.getNewObjectType().equals(Variable.class)) {
			return new CreateVariableCommand(
					getLocation(request),
					(DiagramType) getHost().getModel(),
					editor.getCoordinates());
		} else if (request.getNewObjectType().equals(InParameter.class)) {
			return new CreateParameterCommand(
					getLocation(request),
					(DiagramType) getHost().getModel(),
					editor.getCoordinates());
		}
		return null; 
	}

	private Point getLocation(CreateRequest request) {
		Point loc = request.getLocation().getCopy();
		ZoomManager zoomManager = editor.getAdapter(ZoomManager.class);
		if (zoomManager != null) {
			loc.x = (int) Math.round(loc.x / zoomManager.getZoom());
			loc.y = (int) Math.round(loc.y / zoomManager.getZoom());
		}
		return loc;
	}
	
	/*
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof ComponentPart) {
			return new MoveComponentFeedbackPolicy((ComponentPart) child);
		} else {
			return new ResizableEditPolicy();
		}
	}
	*/
}
