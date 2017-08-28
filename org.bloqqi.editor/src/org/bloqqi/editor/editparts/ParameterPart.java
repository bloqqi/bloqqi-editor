package org.bloqqi.editor.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.editor.Properties;
import org.bloqqi.editor.figures.ParameterFigure;
import org.bloqqi.editor.policies.NodeGraphicalNodeEditPolicy;
import org.bloqqi.editor.policies.ParameterComponentEditPolicy;

abstract public class ParameterPart<T extends Parameter> extends AbstractNodePart<T>
		implements ASTObserver, NodeEditPart {
	
	public ParameterPart(T parameter) {
		super(parameter);
	}

	@Override
	abstract protected IFigure createFigure();

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
		super.deactivate();
	}
	
	@Override
	protected List<Connection> getModelSourceConnections() {
		return new ArrayList<Connection>(getModel().outgoingConnections());
	}
	
	@Override
	protected List<Connection> getModelTargetConnections() {
		return new ArrayList<Connection>(getModel().ingoingConnections());
	}

	@Override
	public void refreshVisuals() {
		ParameterFigure figure = (ParameterFigure) getFigure();
		Parameter parameter = getModel();
		DiagramTypePart parent = (DiagramTypePart) getParent();

		figure.setIsInherited(parameter.isInherited());
		int whatToShow = Properties.instance().getInt(Properties.KEY_BLOCK_NAME, 0);
		String text;
		switch (whatToShow) {
		case 0:
		case 1:
			text = parameter.name();
			break;
		case 2:
		default:
			text = "";
			break;
		}
		figure.getLabel().setText(text);

//		Rectangle r = new Rectangle(
//				new Point(parameter.getX(), parameter.getY()),
//				new Dimension(parameter.getWidth(), parameter.getHeight()));

		parent.setLayoutConstraint(this, figure, getRectangle());
	}

	public Rectangle getRectangle() {
		return getEditor().getCoordinates().getRectangleNearestSuperType(getDiagramType(), getModel());
	}

	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeGraphicalNodeEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ParameterComponentEditPolicy());
	}
	
	@Override
	public void update(ASTObservable o, Object v) {
		refreshVisuals();
	}
}
