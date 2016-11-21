package org.bloqqi.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.graphics.Color;

public class PortFigure extends RectangleFigure {
	private boolean canDelete;
	private Color feedbackColor;

	public PortFigure() {
		setOpaque(true);
	}
	
	@Override public void paintFigure(Graphics g) {
		if (feedbackColor != null) {
			setBackgroundColor(feedbackColor);
			setForegroundColor(ColorConstants.black);
		} else if (canDelete) {
			setBackgroundColor(MyColors.DARK_BLUE);
			setForegroundColor(MyColors.DARK_BLUE);
		} else {
			setBackgroundColor(ColorConstants.black);
			setForegroundColor(ColorConstants.black);
		}
		super.paintFigure(g);
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public void setFeedbackColor(Color feedbackColor) {
		this.feedbackColor = feedbackColor;
	}
}
