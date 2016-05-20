package org.bloqqi.editor.editparts;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.ComponentParameter;
import org.bloqqi.editor.figures.ComponentFigure;
import org.bloqqi.editor.policies.ComponentComponentEditPolicy;
import org.bloqqi.editor.policies.NodeSelectionEditPolicy;

public class ComponentPart extends AbstractNodePart<Component>
		implements ASTObserver {
	public ComponentPart(Component component) {
		super(component);
	}

	@Override
	protected IFigure createFigure() {
		return new ComponentFigure();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ComponentComponentEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new NodeSelectionEditPolicy());
		installEditPolicy("Snap Feedback", new SnapFeedbackPolicy());
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
		ComponentFigure figure = (ComponentFigure) getFigure();
		Component component = getModel();
		DiagramTypePart parent = (DiagramTypePart) getParent();

		String text = "";
		String name;
		if (component.type().isFunction()) {
			name = component.type().name();
		} else {
			name = component.inlinedName();
			if (component.isNameAmbiguous()) {
				name = component.declaredInDiagramType().name() + ASTNode.DECLARED_IN_SEP + name;
			}
		}
		text = name;
		figure.getLabel().setText(text);

		/*int whatToShow = Properties.instance().getInt(Properties.KEY_COMPONENT_NAME, 0);
		switch (whatToShow) {
		case 0:
			text = name + " (" + component.dfo() + ")";
			break;
		case 1:
			text = name;
			break;
		case 2:
			text = String.valueOf(component.dfo());
			break;
		}
		if (name.lastIndexOf("_") >= 0) {
			name = name.substring(0, name.lastIndexOf("_"));
		}
		figure.getLabel().setText(name + "");
		figure.getLabel().setText(String.valueOf(component.dfo()));*/
		
		figure.setHasInParameters(component.getNumInParameter() > 0);
		figure.setHasOutParameters(component.getNumOutParameter() > 0);
		figure.hasErrors(component.isOnTypeCycle() || component.isDataflowCircular());
		figure.setCanDelete(component.canDelete());
		figure.setIsInlined(component.isInlined());
		figure.setShowInlineFeedback(showInlineFeedback());
		figure.setIsRedeclared(component.isLocallyRedeclared());
		
		Rectangle r = getRectangle();
		
		int x = r.x-ComponentParameterPart.SIZE;
		int width = r.width+ComponentParameterPart.SIZE*2;
		Rectangle newR = new Rectangle(x, r.y, width, r.height);
		parent.setLayoutConstraint(this, figure, newR);
	}

	public Rectangle getRectangle() {
		return getEditor().getCoordinates().getRectangleNearestSuperType(getDiagramType(), getModel());
	}
	
	@Override
	protected List<ComponentParameter> getModelChildren() {
		ArrayList<ComponentParameter> list = new ArrayList<ComponentParameter>();
        for (ComponentParameter cp: getModel().getInParameterList()) {
        	list.add(cp);
        } 
        for (ComponentParameter cp: getModel().getOutParameterList()) {
        	list.add(cp);
        }
        return list;
	}
	
	@Override
	public void refresh() {
		super.refresh();
		for (Object child: getChildren()) {
			((EditPart) child).refresh();
		}
	}
	
	@Override
	public void update(ASTObservable o, Object v) {
		refresh();
	}
}
