package org.bloqqi.editor.editparts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.editor.figures.OutParameterFigure;

public class OutParameterPart extends ParameterPart<OutParameter> {
	public OutParameterPart(OutParameter parameter) {
		super(parameter);
	}
	
	@Override
	public IFigure createFigure() {
		return new OutParameterFigure();
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new OutParameterAnchor(getFigure());
	}
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new OutParameterAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new OutParameterAnchor(getFigure());
	}
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new OutParameterAnchor(getFigure());
	}
	
	public static class OutParameterAnchor extends AbstractConnectionAnchor {
		public OutParameterAnchor(IFigure owner) {
			setOwner(owner);
		}
		@Override
		public Point getLocation(Point reference) {
			Point ref = getOwner().getBounds().getLeft();
			getOwner().translateToAbsolute(ref);
			return ref;
		}
	}
}
