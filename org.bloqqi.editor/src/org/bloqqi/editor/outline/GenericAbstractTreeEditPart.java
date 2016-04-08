package org.bloqqi.editor.outline;

import org.eclipse.gef.editparts.AbstractTreeEditPart;

public class GenericAbstractTreeEditPart<T> extends AbstractTreeEditPart {
	public GenericAbstractTreeEditPart(T model) {
		super(model);
	}
	
	@SuppressWarnings("unchecked")
	public T getModel() {
		return (T) super.getModel();
	}
	
	public void refreshVisualsRecursive() {
		refreshVisuals();
		for (Object child: getChildren()) {
			((GenericAbstractTreeEditPart<?>) child).refreshVisualsRecursive();
		}
	}
}
