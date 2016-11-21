package org.bloqqi.editor.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.swt.SWT;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.Literal;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.compiler.ast.Variable;
import org.bloqqi.editor.policies.DiagramTypeXYLayoutEditPolicy;

public class DiagramTypePart extends GenericAbstractGraphicalPart<DiagramType> 
		implements ASTObserver {
	
	public DiagramTypePart(DiagramType diagramType) {
		super(diagramType);
	}

	@Override 
	public void refreshVisuals() {
		ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setAntialias(SWT.ON);
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
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		layer.setBorder(new LineBorder(1));
		return layer;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, 
				new DiagramTypeXYLayoutEditPolicy(getEditor()));
	}
	
	@Override
	protected List<Node> getModelChildren() {
		ArrayList<Node> list = new ArrayList<Node>();
		for (InParameter p: getModel().getInParameters()) {
			list.add(p);
		}
		for (OutParameter p: getModel().getOutParameters()) {
			list.add(p);
		}
		for (Block b: getModel().getBlocks()) {
			list.add(b);
		}
		for (Literal l: getModel().literals()) {
			list.add(l);
		}
		for (Variable v: getModel().variables()) {
			list.add(v);
		}
		return list;
	}

	@Override
	public void update(ASTObservable arg0, Object arg1) {
		refresh();
		for (Object child: getChildren()) {
			((EditPart) child).refresh();
		}
	}
}
