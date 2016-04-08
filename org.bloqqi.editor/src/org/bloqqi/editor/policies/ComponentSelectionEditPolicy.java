package org.bloqqi.editor.policies;

import org.eclipse.gef.EditPart;

import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.editor.editparts.ComponentPart;

public class ComponentSelectionEditPolicy extends SelectionEditPolicy {

	@Override
	protected void hideSelection() {
		hideInlineFeedback(getHost().getRoot());
	}

	@Override
	protected void showSelection() {
		if (getHost() instanceof ComponentPart) {
			ComponentPart cp = (ComponentPart) getHost();
			if (cp.getModel().isInlined()) {
				showInlineFeedback(getHost().getRoot(), cp.getModel());
			}
		}
	}

	protected void showInlineFeedback(EditPart part, Component selectedComponent) {
		if (part instanceof ComponentPart) {
			ComponentPart cp = (ComponentPart) part;
			if (cp.getModel().inlinedComponent() == selectedComponent.inlinedComponent()) {
				cp.setShowInlineFeedback(true);
			}
		} else {
			for (Object c: part.getChildren()) {
				showInlineFeedback((EditPart) c, selectedComponent);
			}
		}
	}

	protected void hideInlineFeedback(EditPart part) {
		if (part instanceof ComponentPart) {
			ComponentPart cp = (ComponentPart) part;
			cp.setShowInlineFeedback(false);
		} else {
			for (Object c: part.getChildren()) {
				hideInlineFeedback((EditPart) c);
			}
		}
	}
}
