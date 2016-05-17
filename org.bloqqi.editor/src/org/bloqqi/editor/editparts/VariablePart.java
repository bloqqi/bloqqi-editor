package org.bloqqi.editor.editparts;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.Variable;
import org.bloqqi.editor.figures.VariableFigure;
import org.bloqqi.editor.policies.NodeGraphicalNodeEditPolicy;
import org.bloqqi.editor.policies.NodeSelectionEditPolicy;
import org.bloqqi.editor.policies.VariableComponentEditPolicy;

public class VariablePart extends AbstractNodePart<Variable> 
		implements ASTObserver, NodeEditPart {
	public VariablePart(Variable variable) {
		super(variable);
	}

	@Override
	protected IFigure createFigure() {
		return new VariableFigure();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new VariableComponentEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new NodeSelectionEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, 
				new NodeGraphicalNodeEditPolicy());
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
		super.deactivate();
	}

	@Override
	public void refreshVisuals() {
		VariableFigure figure = (VariableFigure) getFigure();
		DiagramTypePart parent = (DiagramTypePart) getParent();
		
		figure.setName(getModel().inlinedName());
		figure.setType(getModel().type().name());
		figure.setIsInherited(getModel().isInherited());
		figure.setIsInlined(getModel().isInlined());
		figure.setShowInlineFeedback(showInlineFeedback());
		parent.setLayoutConstraint(this, figure, getRectangle());
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
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}
	
	
	public Rectangle getRectangle() {
		return getEditor().getCoordinates().getRectangleNearestSuperType(getDiagramType(), getModel());
	}
	
	@Override
	public void update(ASTObservable o, Object v) {
		refresh();
	}
}
