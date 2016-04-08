package org.bloqqi.editor.figures;

import org.bloqqi.editor.editparts.ComponentParameterPart;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.LineAttributes;


public class ComponentFigure extends Figure {
	public static final Color INHERITED_COLOR = new Color(null, 220, 220, 220);
//	public static final Color INLINE_COLOR = new Color(null, 202, 202, 232);
	public static final Color INLINE_COLOR = ColorConstants.white;
//	public static final Color INLINE_COLOR = new Color(null, 195, 195, 219);
	public static final Color INLINE_FEEDBACK_COLOR = ColorConstants.lightBlue;
//	Color lightBlue = new Color(null, 127, 127, 255);

	public static final int MIN_HEIGHT = 20;
	public static final int WIDTH = 60;

	private final Label label;
	private final RoundedRectangle rectangle;
	private final RoundedRectangle innerRectangle;

	private boolean hasInParameters;
	private boolean hasOutParameters;
	private boolean isCircular;
	private boolean canDelete;
	private boolean isInlined;
	private boolean showInlineFeedback;
	private boolean isRedeclared;
	
	public ComponentFigure() {
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);
	    
		rectangle = new RoundedRectangle();
		rectangle.setAntialias(SWT.ON);
		rectangle.setLineWidth(1);
		innerRectangle = new RoundedRectangle();
		innerRectangle.setAntialias(SWT.ON);
		innerRectangle.setLineWidth(0);
		label = new Label();
		
		add(rectangle);
		add(innerRectangle);
		add(label);
	}

	public Label getLabel() {
		return label;
	}

	@Override
	public void paintFigure(Graphics g) {
		updateVisual();
		updateCoordinates();
		invalidateFigures();
		super.paintFigure(g);
	}

	private void updateVisual() {
		Color textColor = isCircular ? ColorConstants.red : ColorConstants.black;
		Color borderColor;
		if (canDelete) {
			rectangle.setLineStyle(SWT.LINE_SOLID);
			setBackgroundColor(ColorConstants.white);
			borderColor = ColorConstants.blue;
		} else {
			LineAttributes attributesInherited = new LineAttributes(1);
			attributesInherited.style = SWT.LINE_CUSTOM;
			attributesInherited.dash = new float[]{4, 2};
			rectangle.setLineAttributes(attributesInherited);
			if (showInlineFeedback) {
				setBackgroundColor(INLINE_FEEDBACK_COLOR);
			} else if (isInlined) {
				setBackgroundColor(INLINE_COLOR);
			} else {
				setBackgroundColor(INHERITED_COLOR);
			}
			borderColor = ColorConstants.black;
		}
		borderColor = isCircular ? ColorConstants.red : borderColor;
		rectangle.setForegroundColor(borderColor);
		label.setForegroundColor(textColor);

		if (isRedeclared) {
			innerRectangle.setBackgroundColor(INHERITED_COLOR);
			innerRectangle.setForegroundColor(INHERITED_COLOR);
			innerRectangle.setVisible(true);
		} else {
			innerRectangle.setVisible(false);
		}
	}
	
	private void updateCoordinates() {
		Rectangle r = getBounds().getCopy(); 
		int x = hasInParameters ? ComponentParameterPart.SIZE : 0;
		int w = r.width;
		if (hasInParameters) w -= ComponentParameterPart.SIZE;
		if (hasOutParameters) w -= ComponentParameterPart.SIZE;

		setConstraint(rectangle, new Rectangle(x, 0, w, r.height));
		setConstraint(innerRectangle, new Rectangle(x+1, 0+1, w-2, r.height/2));
		int labelY = r.height/2-10;
		setConstraint(label, new Rectangle(x, labelY, w, 20));
	}
	
	private void invalidateFigures() {
		label.invalidate();
		rectangle.invalidate();
		innerRectangle.invalidate();
	}

	
	public void setHasInParameters(boolean hasInParameters) {
		this.hasInParameters = hasInParameters;
	}
	public void setHasOutParameters(boolean hasOutParameters) {
		this.hasOutParameters = hasOutParameters;
	}
	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	public void setIsInlined(boolean isInlined) {
		this.isInlined = isInlined;
	}
	public void setIsCircular(boolean isCircular) {
		this.isCircular = isCircular;
	}
	public void setShowInlineFeedback(boolean showInlineFeedback) {
		this.showInlineFeedback = showInlineFeedback;
	}
	public void setIsRedeclared(boolean isRedeclared) {
		this.isRedeclared = isRedeclared;
	}
}
