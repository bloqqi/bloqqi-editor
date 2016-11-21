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


public class BlockFigure extends Figure {
	public static final int MIN_HEIGHT = 20;
	public static final int WIDTH = 60;

	private final Label label;
	private final RoundedRectangle rectangle;
	private final RoundedRectangle innerRectangle;

	private boolean hasInParameters;
	private boolean hasOutParameters;
	private boolean hasErrors;
	private boolean canDelete;
	private boolean isInlined;
	private boolean showInlineFeedback;
	private boolean isRedeclared;
	
	public BlockFigure() {
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
		Color textColor = hasErrors ? ColorConstants.red : ColorConstants.black;
		Color borderColor;
		if (canDelete) {
			rectangle.setLineStyle(SWT.LINE_SOLID);
			setBackgroundColor(ColorConstants.white);
			borderColor = ColorConstants.blue;
		} else {
			rectangle.setLineAttributes(FigureUtils.LINE_ATTRIBUTES_INHERITED);
			if (showInlineFeedback) {
				setBackgroundColor(FigureUtils.INLINE_FEEDBACK_COLOR);
			} else if (isInlined) {
				setBackgroundColor(FigureUtils.INLINE_COLOR);
			} else {
				setBackgroundColor(FigureUtils.INHERITED_COLOR);
			}
			borderColor = ColorConstants.black;
		}
		borderColor = hasErrors ? ColorConstants.red : borderColor;
		rectangle.setForegroundColor(borderColor);
		label.setForegroundColor(textColor);

		if (isRedeclared) {
			innerRectangle.setBackgroundColor(FigureUtils.INHERITED_COLOR);
			innerRectangle.setForegroundColor(FigureUtils.INHERITED_COLOR);
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
	public void hasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}
	public void setShowInlineFeedback(boolean showInlineFeedback) {
		this.showInlineFeedback = showInlineFeedback;
	}
	public void setIsRedeclared(boolean isRedeclared) {
		this.isRedeclared = isRedeclared;
	}
}
