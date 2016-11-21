package org.bloqqi.editor.tools;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.editparts.PortPart;

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
		updatePorts(true, ParameterKind.IN);
	}

	public void showOutPorts() {
		updatePorts(true, ParameterKind.OUT);
	}

	public void hide() {
		updatePorts(false, ParameterKind.NONE);
	}

	private void updatePorts(boolean showFeedback, ParameterKind kind) {
		if (rootPart != null) {
			updatePorts(rootPart, showFeedback, kind);
		}
	}
	private void updatePorts(EditPart part, boolean showFeedback, ParameterKind kind) {
		if (part != null) {
			if (part instanceof PortPart) {
				showPortFeedback((PortPart) part, showFeedback, kind);
			} else {
				for (Object o: part.getChildren()) {
					EditPart c = (EditPart) o;
					updatePorts(c, showFeedback, kind);
				}
			}
		}
	}

	private void showPortFeedback(PortPart portPart, boolean showFeedback, ParameterKind kind) {
		boolean canAccess = portPart.getModel().canAccess() || portPart.getModel().canModifyToAccess();
		boolean showInPort = showFeedback && kind == ParameterKind.IN && portPart.getModel().isInParameter();
		boolean showOutPort = showFeedback && kind == ParameterKind.OUT && !portPart.getModel().isInParameter();
		if (canAccess && (showInPort || showOutPort)) {
			if (isTypeCompatible(portPart.getModel().type())
					&& portPart.getModel().ingoingConnections().isEmpty()) {
				portPart.showFeedback(FEEDBACK_VALID_ANCHOR, true);
			} else {
				portPart.showFeedback(FEEDBACK_INVALID_ANCHOR, false);
			}
		} else {
			portPart.hideFeedback();
		}
	}
	
	private boolean isTypeCompatible(TypeDecl other) {
		return type == null || type != null && type.compatibleWith(other);
	}
}