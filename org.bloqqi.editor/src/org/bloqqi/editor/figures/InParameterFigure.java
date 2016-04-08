package org.bloqqi.editor.figures;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Rectangle;

public class InParameterFigure extends ParameterFigure {
	private final Triangle triangleInPar;
	
	public InParameterFigure() {
		triangleInPar = new Triangle();
		triangleInPar.setDirection(PositionConstants.EAST);
		add(triangleInPar);
	}
	
	@Override public void paintFigure(Graphics g) {
		super.paintFigure(g);

		if (isInherited()) {
			triangleInPar.setForegroundColor(ColorConstants.black);
			triangleInPar.setBackgroundColor(ColorConstants.black);
		} else {
			triangleInPar.setForegroundColor(MyColors.DARK_BLUE);
			triangleInPar.setBackgroundColor(MyColors.DARK_BLUE);
		}
		
		Rectangle r = getBounds().getCopy();
		int h = r.height;
		int w = r.width;
		setConstraint(triangleInPar, new Rectangle(w-h/5, 0, h/5, h));
		
		triangleInPar.invalidate();
	}
}
