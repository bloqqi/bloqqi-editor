package org.bloqqi.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.LineAttributes;

abstract public class ParameterFigure extends Figure {
	public static final int HEIGHT = 30;
	public static final int WIDTH = 80;

	private final RectangleFigure rectangle;
	private final Triangle triangle;
	private final Label label;
	private boolean isInherited;
	
	public ParameterFigure() {
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);
	    
		rectangle = new RectangleFigure();
		add(rectangle);
		
		triangle = new Triangle();
		triangle.setDirection(PositionConstants.EAST);
		add(triangle);
		
		
		label = new Label();
		add(label);
	}

	public void paintFigure(Graphics g) {
		super.paintFigure(g);
		
		Rectangle r = getBounds().getCopy(); 
		
		if (isInherited) {
			rectangle.setForegroundColor(ColorConstants.black);
			rectangle.setBackgroundColor(ComponentFigure.INHERITED_COLOR);
			rectangle.setLineAttributes(createLineAttributesInherited());
			
			triangle.setForegroundColor(ColorConstants.black);
			triangle.setBackgroundColor(ComponentFigure.INHERITED_COLOR);
			triangle.setLineAttributes(createLineAttributesInherited());
		} else {
			rectangle.setForegroundColor(ColorConstants.blue);
			rectangle.setBackgroundColor(ColorConstants.white);
			rectangle.setLineStyle(SWT.LINE_SOLID);
			
			triangle.setForegroundColor(ColorConstants.blue);
			triangle.setBackgroundColor(ColorConstants.white);
			triangle.setLineStyle(SWT.LINE_SOLID);
		}
		
		int h = r.height;
		int w = r.width;
		int rectangleWidth = w-h/2;
		setConstraint(rectangle, new Rectangle(0, 0, rectangleWidth+1, h));
		setConstraint(triangle, new Rectangle(rectangleWidth, 0, h/2, h));
		setConstraint(label, new Rectangle(0, h/2-10, rectangleWidth, 20));
		
		rectangle.invalidate();
		triangle.invalidate();
	    label.invalidate();
	}

	private LineAttributes createLineAttributesInherited() {
		LineAttributes attributesInherited = new LineAttributes(1);
		attributesInherited.style = SWT.LINE_CUSTOM;
		attributesInherited.dash = new float[]{4, 2};
		return attributesInherited;
	}
	
	public void setIsInherited(boolean isInherited) {
		this.isInherited = isInherited;
	}
	
	public boolean isInherited() {
		return isInherited;
	}

	public Label getLabel() {
		return label;
	}
}
