package org.bloqqi.editor.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.Literal;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.Port;
import org.bloqqi.editor.policies.LiteralComponentEditPolicy;

public class LiteralPart extends GenericAbstractGraphicalPart<Literal> implements ASTObserver {
	public LiteralPart(Literal literal) {
		super(literal);
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
		Label label= (Label) getFigure();
		label.setText(getModel().toString());
		DiagramTypePart parent = (DiagramTypePart) getParent();

		Node target = getModel().enclosingConnection().getTarget().node();
		Anchor anchor = getModel().enclosingConnection().getTarget().anchor();
		Rectangle r = getEditor().getCoordinates().getRectangle(getDiagramType(), target.accessString());
		Dimension d = label.getPreferredSize();
		r.x -= d.width + 12;
		r.height = d.height;
		r.width = d.width;
		if (anchor instanceof Port) {
			r.y += PortPart.getYPos((Port) anchor) - 3;
		}
		parent.setLayoutConstraint(this, figure, r);
	}

	
	@Override
	protected IFigure createFigure() {
		return new Label();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new LiteralComponentEditPolicy());
	}

	@Override
	public void update(ASTObservable o, Object arg) {
		refresh();
	}

	@Override
	protected List<Connection> getModelSourceConnections() {
		return new ArrayList<>(getModel().outgoingConnections());
	}
	
	@Override
	protected List<Connection> getModelTargetConnections() { 
		return new ArrayList<>(getModel().ingoingConnections());
	}
}
