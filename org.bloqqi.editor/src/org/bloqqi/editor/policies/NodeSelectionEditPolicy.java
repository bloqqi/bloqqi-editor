package org.bloqqi.editor.policies;

import org.eclipse.gef.EditPart;

import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.editor.editparts.AbstractNodePart;

public class NodeSelectionEditPolicy extends SelectionEditPolicy {

	@Override
	protected void hideSelection() {
		hideInlineFeedback(getHost().getRoot());
	}

	@Override
	protected void showSelection() {
		if (getHost() instanceof AbstractNodePart) {
			AbstractNodePart<?> np = (AbstractNodePart<?>) getHost();
			if (np.getModel().isInlined()) {
				showInlineFeedback(getHost().getRoot(), np.getModel().inlinedComponent());
			}
		}
	}

	protected void showInlineFeedback(EditPart part, Component inlinedComponent) {
		if (part instanceof AbstractNodePart) {
			AbstractNodePart<?> np = (AbstractNodePart<?>) part;
			if (np.getModel().inlinedComponent() == inlinedComponent) {
				np.setShowInlineFeedback(true);
			}
		} else {
			for (Object c: part.getChildren()) {
				showInlineFeedback((EditPart) c, inlinedComponent);
			}
		}
	}

	protected void hideInlineFeedback(EditPart part) {
		if (part instanceof AbstractNodePart) {
			AbstractNodePart<?> np = (AbstractNodePart<?>) part;
			np.setShowInlineFeedback(false);
		} else {
			for (Object c: part.getChildren()) {
				hideInlineFeedback((EditPart) c);
			}
		}
	}
}
