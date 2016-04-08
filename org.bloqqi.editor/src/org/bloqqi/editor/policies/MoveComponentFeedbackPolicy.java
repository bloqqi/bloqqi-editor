package org.bloqqi.editor.policies;

import java.util.TreeSet;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.swt.graphics.Color;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.editparts.ComponentParameterPart;
import org.bloqqi.editor.editparts.ComponentPart;

public class MoveComponentFeedbackPolicy extends ResizableEditPolicy {
	private static Color DARK_RED = new Color(null, 135, 0, 0);

	private final ComponentPart componentPart;
	private final BloqqiEditor editor;
	
	private IFigure sinkLowerFigure;
	private IFigure sinkUpperFigure;
	private IFigure cycleLowerFigure;
	private IFigure cycleUpperFigure;

	public MoveComponentFeedbackPolicy(ComponentPart componentPart, BloqqiEditor editor) {
		this.componentPart = componentPart;
		this.editor = editor;
	}

	@Override
	public void showSourceFeedback(Request request) {
		super.showSourceFeedback(request);
		if (request instanceof ChangeBoundsRequest) {
			ChangeBoundsRequest c = (ChangeBoundsRequest) request;
			if (c.getEditParts().size() == 1) {
				dfoChangeFeedback();
			}
		}
	}

	@Override
	public void eraseSourceFeedback(Request request) {
		super.eraseSourceFeedback(request);
		if (sinkLowerFigure != null) removeFeedback(sinkLowerFigure);
		if (sinkUpperFigure != null) removeFeedback(sinkUpperFigure);
		if (cycleLowerFigure != null) removeFeedback(cycleLowerFigure);
		if (cycleUpperFigure != null) removeFeedback(cycleUpperFigure);
		sinkLowerFigure = null;
		sinkUpperFigure = null;
		cycleLowerFigure = null;
		cycleUpperFigure = null;
	}
	
	@Override
	protected Rectangle getInitialFeedbackBounds() {
		Rectangle r = super.getInitialFeedbackBounds().getCopy();
		r.x += ComponentParameterPart.SIZE;
		r.width -= ComponentParameterPart.SIZE*2;
		return r;
	}

	private void dfoChangeFeedback() {
		int sinkLowerBound = createSinkLowerBound();
		int sinkUpperBound = createSinkUpperBound();

		int cycleLowerBound = createCycleLowerBound();
		int cycleUpperBound = createCycleUpperBound();
		
		int lowerBound = Math.max(sinkLowerBound, cycleLowerBound);
		int upperBound = Math.min(sinkUpperBound, cycleUpperBound);
		
		setComponentColor(lowerBound, upperBound);
	}

	private int createSinkLowerBound() {
		int lowerBound = Integer.MIN_VALUE;
		TreeSet<Component> sinkSiblings = componentPart.getModel().sinkSiblings();
		Component lowerSibling = sinkSiblings.lower(componentPart.getModel());
		if (lowerSibling != null) {
			lowerBound = distance(lowerSibling);
			if (sinkLowerFigure == null) {
				sinkLowerFigure = createCircle(lowerBound);
				addFeedback(sinkLowerFigure);
			}
		}
		return lowerBound;
	}
	
	private int createSinkUpperBound() {
		int upperBound = Integer.MAX_VALUE;
		TreeSet<Component> sinkSiblings = componentPart.getModel().sinkSiblings();
		Component upperSibling = sinkSiblings.higher(componentPart.getModel());
		if (upperSibling != null) {
			upperBound = distance(upperSibling);
			if (sinkUpperFigure == null) {
				sinkUpperFigure = createCircle(upperBound);
				addFeedback(sinkUpperFigure);
			}
		}
		return upperBound;
	}

	private int createCycleLowerBound() {
		int lowerBound = Integer.MIN_VALUE;
		Component lowerComponent = componentPart.getModel().cycleLowerBound();
		if (lowerComponent != null) {
			lowerBound = distance(lowerComponent);
			if (cycleLowerFigure == null) {
				cycleLowerFigure = createCircle(lowerBound, DARK_RED);
				addFeedback(cycleLowerFigure);
			}
		}
		return lowerBound;
	}
	
	private int createCycleUpperBound() {
		int upperBound = Integer.MAX_VALUE;
		Component upperComponent = componentPart.getModel().cycleUpperBound();
		if (upperComponent != null) {
			upperBound = distance(upperComponent);
			if (cycleUpperFigure == null) {
				cycleUpperFigure = createCircle(upperBound, DARK_RED);
				addFeedback(cycleUpperFigure);
			}
		}
		return upperBound;
	}


	private void setComponentColor(int lowerBound, int upperBound) {
		IFigure feedback = getDragSourceFeedbackFigure();
		int distance = distance(feedback.getBounds().x, feedback.getBounds().y);
		((Shape) feedback).setFillXOR(false);
		if (distance < getZoom()*lowerBound || distance > getZoom()*upperBound) {
			feedback.setBackgroundColor(ColorConstants.yellow);
		} else {
			feedback.setBackgroundColor(ColorConstants.green);							
		}
	}
	
	private IFigure createCircle(int radius) {
		return createCircle(radius, ColorConstants.black);
	}
	private IFigure createCircle(int radius, Color color) {
		MyCircle c = new MyCircle();
		int diameter = (int) Math.round(2*radius*getZoom());
		c.setBounds(new Rectangle(0, 0, diameter, diameter));
		c.setForegroundColor(color);
		c.setLineWidthFloat((float) getZoom());
		return c;
	}

	private int distance(Component c) {
		Rectangle r = editor.getCoordinates().getRectangle(c.diagramType(), c.accessString());
		return distance(r.x, r.y);
	}

	private int distance(int x, int y) {
		double dist = Math.sqrt(x*x + y*y);
		return (int) Math.round(dist);
	}

	private double getZoom() {
		ZoomManager zm = (ZoomManager) componentPart.getViewer().getProperty(ZoomManager.class.toString());
		if (zm != null) {
			return zm.getZoom();
		}
		return 1;
	}

	private static class MyCircle extends Shape {
		public boolean containsPoint(int x, int y) {
			return false;
		}

		protected void fillShape(Graphics graphics) {
			// graphics.fillOval(getOptimizedBounds());
		}

		protected void outlineShape(Graphics graphics) {
			graphics.drawOval(getOptimizedBounds());
		}

		private Rectangle getOptimizedBounds() {
			Rectangle r = new Rectangle(getBounds());
			r.x -= r.width/2;
			r.y -= r.height/2;
			return r;
		}
	}
}
