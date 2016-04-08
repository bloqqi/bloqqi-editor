package org.bloqqi.editor.editparts;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.graphics.Color;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.ComponentParameter;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.editor.figures.ComponentFigure;
import org.bloqqi.editor.figures.ComponentParameterFigure;
import org.bloqqi.editor.policies.ComponentGraphicalNodeEditPolicy;

public class ComponentParameterPart
		extends GenericAbstractGraphicalPart<ComponentParameter>
		implements NodeEditPart, ASTObserver {

	public final static int SIZE = 6;
	public final static int PADDING = 8;
	public final static int Y0 = 5;
	
	private boolean feedbackLargeSize;
	private Color feedbackColor;
	private ComponentParameterMouseListener mouseListener;

	public ComponentParameterPart(ComponentParameter par) {
		super(par);
	}
	
	@Override
	protected IFigure createFigure() {
		ComponentParameterFigure figure = new ComponentParameterFigure();
		mouseListener = new ComponentParameterMouseListener();
		figure.addMouseMotionListener(mouseListener);
		return figure;
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new ComponentGraphicalNodeEditPolicy());
	}

	@Override
	public void activate() {
		if (!isActive()) {
			getModel().addObserver(this);
		}
		super.activate();
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			getModel().deleteObserver(this);
		}
		
		// When a connection is created, make sure that
		// the mouse-over feedback is removed.
		mouseListener.removeLabel();
		
		super.deactivate();
	}
	
	@Override
	protected List<Connection> getModelSourceConnections() {
		return new ArrayList<>(getModel().outgoingConnections());
	}
	
	@Override
	protected List<Connection> getModelTargetConnections() { 
		return new ArrayList<>(getModel().ingoingConnections());
	}

	@Override
	public void refresh() {
		super.refresh();
		for (Object o: getTargetConnections()) {
			ConnectionEditPart conn = (ConnectionEditPart) o;
			conn.refresh();
		}
		for (Object o: getSourceConnections()) {
			ConnectionEditPart conn = (ConnectionEditPart) o;
			conn.refresh();
		}
	}
	
	@Override
	public void refreshVisuals() {
		ComponentPart parent = (ComponentPart) getParent();
		ComponentParameterFigure f = (ComponentParameterFigure) getFigure();
		
		int x = getModel().isInParameter() ? 1 : parent.getRectangle().width+SIZE-1;
		int y = getYPos(getModel());
		f.setFeedbackColor(feedbackColor);
		f.setCanDelete(parent.getModel().canDelete());
		if (feedbackLargeSize) {
			x -= getModel().isInParameter() ? 1 : 0;
			parent.setLayoutConstraint(this, f, new Rectangle(x, y, SIZE+1, SIZE+1));
		} else {
			parent.setLayoutConstraint(this, f, new Rectangle(x, y, SIZE, SIZE));
		}
		
		f.invalidate();
	}

	@Override
	public void update(ASTObservable arg0, Object arg1) {
		refresh();
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new InParameterPart.InParameterAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new InParameterPart.InParameterAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new OutParameterPart.OutParameterAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new OutParameterPart.OutParameterAnchor(getFigure());
	}
	
	public boolean isInParameter() {
		return getModel().declaredParameter().isInParameter();
	}
	
	public void showFeedback(Color feedbackColor, boolean feedbackLargeSize) {
		if (this.feedbackColor != feedbackColor || this.feedbackLargeSize != feedbackLargeSize) {
			this.feedbackColor = feedbackColor;
			this.feedbackLargeSize = feedbackLargeSize;
			refreshVisuals();
			getFigure().repaint();
		}
	}
	public void hideFeedback() {
		showFeedback(null, false);
	}
	
	public static int getYPos(ComponentParameter compPar) {
		int y = Y0 + compPar.index() * (SIZE+PADDING);
		return y;
	}
	
	public static int getMidYPos(ComponentParameter compPar) {
		return getYPos(compPar) + (int) Math.round(SIZE/2.0);
	}
	
	public static int computeNodeMinHeight(Node node) {
		if (node instanceof Component) {
			Component c = (Component) node;
			int nIn = c.getNumInParameter();
			int nOut = c.getNumOutParameter();
			int minIn = Y0*2 + nIn*SIZE + (nIn-1)*PADDING;
			int minOut = Y0*2 + nOut*SIZE + (nOut-1)*PADDING;
			return Math.max(Math.max(minIn, minOut), ComponentFigure.MIN_HEIGHT);
		} else {
			return ComponentFigure.MIN_HEIGHT;
		}
	}
	
	private class ComponentParameterMouseListener extends MouseMotionListener.Stub {
		private Label label = null;
		
		@Override
		public void mouseEntered(MouseEvent me) {
			IFigure figure = (IFigure) me.getSource();
			int x = figure.getBounds().x + 12;
			int y = figure.getBounds().y - 4;
			ZoomManager zoomManager = getRoot().getAdapter(ZoomManager.class);
			if (zoomManager != null) {
				x = (int) Math.round(x * zoomManager.getZoom());
				y = (int) Math.round(y * zoomManager.getZoom());
			}

			label = new Label();
			label.setText(getModel().name() + ": " + getModel().type().name());
			Dimension d = label.getPreferredSize();
			label.setBounds(new Rectangle(x, y, d.width+4, d.height+4));
			label.setBackgroundColor(ColorConstants.white);
			label.setOpaque(true);
			label.setBorder(new LineBorder(ColorConstants.gray));
			getFeedbackLayer().add(label);
		}

		@Override
		public void mouseExited(MouseEvent me) {
			removeLabel();
		}

		private void removeLabel() {
			if (label != null) {
				getFeedbackLayer().remove(label);
				label = null;
			}
		}
		
		private IFigure getFeedbackLayer() {
			return getLayer(LayerConstants.FEEDBACK_LAYER);
		}
	}
}
