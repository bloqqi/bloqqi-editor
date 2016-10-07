package org.bloqqi.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

public class VariableFigure extends Figure {
	private RectangleFigure rectangle;
	private Label label;
	
	private String name;
	private String type;
	private boolean isInherited;
	private boolean isInlined;
	private boolean showInlineFeedback;
	private String kind;
	
	public VariableFigure() {
		setLayoutManager(new XYLayout());

		rectangle = new RectangleFigure();
		add(rectangle);
	
		label = new Label();
		add(label);
		
		name = "";
		type = "";
		isInherited = false;
		isInlined = false;
	}
	
	public void paintFigure(Graphics g) {
		super.paintFigure(g);
		
		Rectangle r = getBounds().getCopy(); 
		
		if (isInherited || isInlined) {
			rectangle.setForegroundColor(ColorConstants.black);
			if (showInlineFeedback) {
				rectangle.setBackgroundColor(FigureUtils.INLINE_FEEDBACK_COLOR);
			} else if (isInlined) {
				rectangle.setBackgroundColor(FigureUtils.INLINE_COLOR);
			} else {
				rectangle.setBackgroundColor(FigureUtils.INHERITED_COLOR);
			}
			rectangle.setLineAttributes(FigureUtils.LINE_ATTRIBUTES_INHERITED);
		} else {
			rectangle.setForegroundColor(ColorConstants.blue);
			rectangle.setBackgroundColor(ColorConstants.white);
			rectangle.setLineStyle(SWT.LINE_SOLID);
		}
		
		label.setText(kind + " " + name + ":" + type);
		
		int h = r.height;
		int w = r.width;
		setConstraint(rectangle, new Rectangle(0, 0, w, h));
		setConstraint(label, new Rectangle(0, h/2-10, w, 20));
		
		rectangle.invalidate();
	    label.invalidate();
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setIsInherited(boolean isInherited) {
		this.isInherited = isInherited;
	}
	
	public void setIsInlined(boolean isInlined) {
		this.isInlined = isInlined;
	}

	public void setShowInlineFeedback(boolean showInlineFeedback) {
		this.showInlineFeedback = showInlineFeedback;
	}


}
