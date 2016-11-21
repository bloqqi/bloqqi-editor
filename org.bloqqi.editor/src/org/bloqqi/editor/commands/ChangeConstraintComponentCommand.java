package org.bloqqi.editor.commands;

import org.bloqqi.editor.editparts.PortPart;
import org.bloqqi.editor.figures.BlockFigure;
import org.eclipse.draw2d.geometry.Rectangle;

public class ChangeConstraintComponentCommand extends ChangeConstraintNodeCommand {
	public void setNewConstraint(Rectangle newConstraint) {
		if (newConstraint.height < BlockFigure.MIN_HEIGHT) {
			newConstraint.height = BlockFigure.MIN_HEIGHT;
		}
		Rectangle r = newConstraint.getCopy();
		r.x += PortPart.SIZE;
		r.width -= PortPart.SIZE*2;
		this.newConstraint = r;
	}
}
