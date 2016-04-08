package org.bloqqi.editor.editparts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.editor.figures.InParameterFigure;

public class InParameterPart extends ParameterPart<InParameter> {
	public InParameterPart(InParameter parameter) {
		super(parameter);
	}
	
	@Override
	public IFigure createFigure() {
		return new InParameterFigure();
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new InParameterAnchor(getFigure());
	}
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new InParameterAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new InParameterAnchor(getFigure());
	}
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new InParameterAnchor(getFigure());
	}
	
	public static class InParameterAnchor extends AbstractConnectionAnchor {
		public InParameterAnchor(IFigure owner) {
			setOwner(owner);
		}
		@Override
		public Point getLocation(Point reference) {
			Point ref = getOwner().getBounds().getRight();
			getOwner().translateToAbsolute(ref);
			return ref;
		}
	}
}
