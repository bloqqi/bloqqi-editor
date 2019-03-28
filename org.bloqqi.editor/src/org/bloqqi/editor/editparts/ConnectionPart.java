package org.bloqqi.editor.editparts;


import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.LineAttributes;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.ConnectionInterception;
import org.bloqqi.compiler.ast.InterceptConnection;
import org.bloqqi.editor.figures.SplitPolylineConnection;
import org.bloqqi.editor.policies.ConnectionEditPolicy;

public class ConnectionPart extends AbstractConnectionEditPart {
	private static final Color COLOR = ColorConstants.blue;
	private static final Color INHERITED_COLOR = ColorConstants.black;
	private RectangleFigure rectangleFigure;
	
	public ConnectionPart(Connection connection) { 
		setModel(connection);
	}
	
	public Connection getModel() {
		return (Connection) super.getModel();
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, 
				new ConnectionEndpointEditPolicy());
	}
	
	
	@Override
	public void refreshVisuals() {
		SplitPolylineConnection figure = (SplitPolylineConnection) getFigure();

		Connection conn = getModel();

		LineAttributes attributesInherited = new LineAttributes(1);
		attributesInherited.style = SWT.LINE_CUSTOM;
		attributesInherited.dash = new float[]{4, 2};
		LineAttributes attributesNormal = new LineAttributes(1);
		attributesNormal.style = SWT.LINE_SOLID;
		
		Color foregroundColor = COLOR;
		if (conn.hasErrors() || conn.isOnDataflowCycle()) {
			foregroundColor = ColorConstants.red;
			figure.setLineWidth(2);
		} else {
			figure.setLineAttributes(attributesNormal);
		}
		figure.setForegroundColor(foregroundColor);

		if (!conn.canDelete()) {
			figure.setForegroundColor(INHERITED_COLOR);
			figure.setLineAttributes(attributesInherited);
		} else if (conn.isIntercept()) {
			InterceptConnection ic = (InterceptConnection) conn;
			boolean isConnectionInterception = ic.getDeclaredFlowDecl() instanceof ConnectionInterception;
			if (ic.getIsFirstConnection()) {
				figure.setLineAttributesFirst(attributesInherited);
				figure.setLineAttributesSecond(attributesNormal);
				figure.setColorFirst(INHERITED_COLOR);
				figure.setColorSecond(foregroundColor);
				figure.setLargeRectangle(!isConnectionInterception);
			} else {
				figure.setLineAttributesFirst(attributesNormal);
				figure.setLineAttributesSecond(attributesInherited);
				figure.setColorFirst(foregroundColor);
				figure.setColorSecond(INHERITED_COLOR);
				figure.setLargeRectangle(isConnectionInterception);
			}
		}
		
		figure.invalidate();
	}
	
	@Override
	protected IFigure createFigure() {
		SplitPolylineConnection conn = new SplitPolylineConnection();
		conn.setConnectionRouter(new ManhattanConnectionRouter());
		if (!getModel().getSource().isLiteral()) {
			conn.setTargetDecoration(new PolylineDecoration());
		}
		if (getModel().isIntercept()) {
			rectangleFigure = new RectangleFigure();
			rectangleFigure.setBackgroundColor(COLOR);
			rectangleFigure.setForegroundColor(COLOR);
			conn.add(rectangleFigure, new ConnectionLocator(conn));
			conn.setRectangleFigure(rectangleFigure, new ConnectionLocator(conn));
		}
		return conn;
	}
	
	public void changeConnectionRouter() {
		PolylineConnection pc = (PolylineConnection) getFigure();
		if (pc.getConnectionRouter() == ConnectionRouter.NULL) {
			pc.setConnectionRouter(new ManhattanConnectionRouter());
		} else {
			pc.setConnectionRouter(null);
		}
	}
	
	@Override
	public void refresh() {
		super.refresh();
	}
}
