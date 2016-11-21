package org.bloqqi.editor.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.figures.BlockFigure;

public class ChangeConstraintNodeCommand extends Command {
	protected Coordinates coordinates;
	protected Rectangle newConstraint;
	protected Rectangle oldConstraint;
	protected Node node;

	@Override
	public void execute() {
		if (oldConstraint == null) {
			oldConstraint = coordinates.getRectangle(node.diagramType(), node.accessString());
		}
		updateModel(newConstraint);
	}
	
	@Override
	public void undo() {
		updateModel(oldConstraint);
	}

	public void setNewConstraint(Rectangle newConstraint) {
		if (newConstraint.height < BlockFigure.MIN_HEIGHT) {
			newConstraint.height = BlockFigure.MIN_HEIGHT;
		}
		this.newConstraint = newConstraint;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	protected void updateModel(Rectangle r) {
		coordinates.setRectangle(node.diagramType(), node.accessString(), r);
		node.diagramType().notifyObservers();
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
}
