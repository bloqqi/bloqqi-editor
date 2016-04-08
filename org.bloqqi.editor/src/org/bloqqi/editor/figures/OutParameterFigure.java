package org.bloqqi.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

public class OutParameterFigure extends ParameterFigure {
	private final static int WIDTH = 3;

	private final RectangleFigure rectangleOutPar;
	
	public OutParameterFigure() {
		rectangleOutPar = new RectangleFigure();
		add(rectangleOutPar);
	}

	@Override public void paintFigure(Graphics g) {
		super.paintFigure(g);
		
		if (isInherited()) {
			rectangleOutPar.setForegroundColor(ColorConstants.black);
			rectangleOutPar.setBackgroundColor(ColorConstants.black);
		} else {
			rectangleOutPar.setForegroundColor(MyColors.DARK_BLUE);
			rectangleOutPar.setBackgroundColor(MyColors.DARK_BLUE);
		}
		
		Rectangle r = getBounds();
		setConstraint(rectangleOutPar, new Rectangle(0, 0, WIDTH, r.height));
		
		rectangleOutPar.invalidate();
	}
}
