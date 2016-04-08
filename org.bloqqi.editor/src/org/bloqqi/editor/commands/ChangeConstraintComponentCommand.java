package org.bloqqi.editor.commands;

import org.bloqqi.editor.editparts.ComponentParameterPart;
import org.bloqqi.editor.figures.ComponentFigure;
import org.eclipse.draw2d.geometry.Rectangle;

public class ChangeConstraintComponentCommand extends ChangeConstraintNodeCommand {
	public void setNewConstraint(Rectangle newConstraint) {
		if (newConstraint.height < ComponentFigure.MIN_HEIGHT) {
			newConstraint.height = ComponentFigure.MIN_HEIGHT;
		}
		Rectangle r = newConstraint.getCopy();
		r.x += ComponentParameterPart.SIZE;
		r.width -= ComponentParameterPart.SIZE*2;
		this.newConstraint = r;
	}
}
