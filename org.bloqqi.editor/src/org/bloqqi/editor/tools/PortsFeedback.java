package org.bloqqi.editor.tools;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.editparts.ComponentParameterPart;

public class PortsFeedback {
	public static final Color FEEDBACK_VALID_ANCHOR = ColorConstants.green;
	public static final Color FEEDBACK_INVALID_ANCHOR = ColorConstants.yellow;
	
	private final EditPart rootPart;
	private final TypeDecl type;
	
	private enum ParameterKind {
		IN,
		OUT,
		NONE,
	}
	
	public PortsFeedback(EditPart rootPart) {
		this(rootPart, null);
	}
	public PortsFeedback(EditPart rootPart, TypeDecl type) {
		this.rootPart = rootPart;
		this.type = type;
	}
	
	public void showInPorts() {
		updateComponentParameters(true, ParameterKind.IN);
	}

	public void showOutPorts() {
		updateComponentParameters(true, ParameterKind.OUT);
	}

	public void hide() {
		updateComponentParameters(false, ParameterKind.NONE);
	}

	private void updateComponentParameters(boolean showFeedback, ParameterKind kind) {
		if (rootPart != null) {
			updateComponentParameters(rootPart, showFeedback, kind);
		}
	}
	private void updateComponentParameters(EditPart part, boolean showFeedback, ParameterKind kind) {
		if (part != null) {
			if (part instanceof ComponentParameterPart) {
				showComponentParameterFeedback((ComponentParameterPart) part, showFeedback, kind);
			} else {
				for (Object o: part.getChildren()) {
					EditPart c = (EditPart) o;
					updateComponentParameters(c, showFeedback, kind);
				}
			}
		}
	}

	private void showComponentParameterFeedback(ComponentParameterPart cpp, boolean showFeedback, ParameterKind kind) {
		boolean canAccess = cpp.getModel().canAccess() || cpp.getModel().canModifyToAccess();
		boolean showInPort = showFeedback && kind == ParameterKind.IN && cpp.getModel().isInParameter();
		boolean showOutPort = showFeedback && kind == ParameterKind.OUT && !cpp.getModel().isInParameter();
		if (canAccess && (showInPort || showOutPort)) {
			if (isTypeCompatible(cpp.getModel().type())
					&& cpp.getModel().ingoingConnections().isEmpty()) {
				cpp.showFeedback(FEEDBACK_VALID_ANCHOR, true);
			} else {
				cpp.showFeedback(FEEDBACK_INVALID_ANCHOR, false);
			}
		} else {
			cpp.hideFeedback();
		}
	}
	
	private boolean isTypeCompatible(TypeDecl other) {
		return type == null || type != null && type.compatibleWith(other);
	}
}