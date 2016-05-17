package org.bloqqi.editor.editparts;

import org.bloqqi.compiler.ast.Node;

public abstract class AbstractNodePart<T extends Node> extends GenericAbstractGraphicalPart<T> {
	private boolean showInlineFeedback;
	
	public AbstractNodePart(T model) {
		super(model);
	}
	
	public boolean showInlineFeedback() {
		return showInlineFeedback;
	}
	
	public void setShowInlineFeedback(boolean showInlineFeedback) {
		if (this.showInlineFeedback != showInlineFeedback) {
			this.showInlineFeedback = showInlineFeedback;
			refreshVisuals();
			getFigure().repaint();
		}
	}
}
