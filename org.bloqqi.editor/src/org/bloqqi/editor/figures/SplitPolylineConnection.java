package org.bloqqi.editor.figures;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.LineAttributes;

public class SplitPolylineConnection extends PolylineConnection {
	private PointList beforePoints;
	private PointList afterPoints;
	private LineAttributes lineAttributesFirst;
	private LineAttributes lineAttributesSecond;
	private Color colorFirst;
	private Color colorSecond;
	private RectangleFigure rectangleFigure;
	private boolean largeRectangle;
	
	@Override
	public void setPoints(PointList list) {
		super.setPoints(list);
		
		if (lineAttributesFirst != lineAttributesSecond || colorFirst != colorSecond) {
			PointList points = getPoints();
			beforePoints = new PointList();
			afterPoints = new PointList();
			for (int i = 0; i < points.size(); i++) {
				if (i < points.size()/2) {
					beforePoints.addPoint(points.getPoint(i));
				} else {
					afterPoints.addPoint(points.getPoint(i));
				}
			}
			if (points.size()%2 == 0) {
				Point p1 = beforePoints.getLastPoint();
				Point p2 = afterPoints.getFirstPoint();
				Point mid = new Point((p1.x()+p2.x())/2, (p1.y()+p2.y())/2);
				beforePoints.addPoint(mid.getCopy());
				afterPoints.insertPoint(mid.getCopy(), 0);
			} else {
				beforePoints.addPoint(afterPoints.getFirstPoint().getCopy());
			}
	
			Point p1 = beforePoints.getPoint(beforePoints.size()-2);
			Point p2 = beforePoints.getPoint(beforePoints.size()-1);
			Rectangle rectangle;
			if (largeRectangle) {
				rectangle = new Rectangle(-4, -2, 8, 4);
			} else {
				rectangle = new Rectangle(-3, -1, 7, 2);
			}
			if (p1.getDistance(p2) > 5 && p1.x == p2.x) {
				rectangleFigure.setBounds(rectangle);
			} else {
				rectangleFigure.setBounds(rectangle.transpose());
			}
		}
	}

	protected void outlineShape(Graphics g) {
		if (lineAttributesFirst == lineAttributesSecond && colorFirst == colorSecond) {
			g.drawPolyline(getPoints());
		} else {
			outlineSplitShape(g);
		}
	}

	private void outlineSplitShape(Graphics g) {
		g.setLineAttributes(lineAttributesFirst);
		g.setForegroundColor(colorFirst);
		g.drawPolyline(beforePoints);

		g.setLineAttributes(lineAttributesSecond);
		g.setForegroundColor(colorSecond);
		g.drawPolyline(afterPoints);
	}

	@Override 
	public void setLineAttributes(LineAttributes lineAttributes) {
		super.setLineAttributes(lineAttributes);
		setLineAttributesFirst(lineAttributes);
		setLineAttributesSecond(lineAttributes);
	}

	@Override
	public void setForegroundColor(Color color) {
		super.setForegroundColor(color);
		this.colorFirst = color;
		this.colorSecond = color;
	}

	public void setLineAttributesFirst(LineAttributes lineAttributesFirst) {
		this.lineAttributesFirst = lineAttributesFirst;
	}

	public void setLineAttributesSecond(LineAttributes lineAttributesSecond) {
		this.lineAttributesSecond = lineAttributesSecond;
	}

	public void setColorFirst(Color color) {
		if (getSourceDecoration() != null) {
			getSourceDecoration().setForegroundColor(color);
		}
		this.colorFirst = color;
	}

	public void setColorSecond(Color color) {
		if (getTargetDecoration() != null) {
			getTargetDecoration().setForegroundColor(color);
		}
		this.colorSecond = color;
	}

	public void setRectangleFigure(RectangleFigure rectangleFigure,
			ConnectionLocator connectionLocator) {
		this.rectangleFigure = rectangleFigure;
		add(rectangleFigure, connectionLocator);
	}
	
	public void setLargeRectangle(boolean largeRectangle) {
		this.largeRectangle = largeRectangle;
	}
}