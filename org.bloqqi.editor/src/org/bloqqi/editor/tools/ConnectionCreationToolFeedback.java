package org.bloqqi.editor.tools;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.TypeDecl;

public class ConnectionCreationToolFeedback extends AbstractConnectionCreationTool {
	private EditPart rootEditPart;
	private PortsFeedback portsFeedback;

	@Override
	public void activate() {
		super.activate();
		portsFeedback = new PortsFeedback(rootEditPart);
		portsFeedback.showOutPorts();
	}
	
	@Override
	public void deactivate() {
		hideFeedback();
		super.deactivate();
	}
	
	@Override
	protected void setState(int state) {
		if (state == STATE_CONNECTION_STARTED) {
			hideFeedback();
		}
		super.setState(state);
	}
	
	@Override
	public void showSourceFeedback() {
		Request request = getSourceRequest();
		if (portsFeedback == null && REQ_CONNECTION_END.equals(request.getType())) {
			CreateConnectionRequest createReq = (CreateConnectionRequest) request;
			RootEditPart root = createReq.getSourceEditPart().getRoot();
			Anchor anchor = (Anchor) createReq.getSourceEditPart().getModel();
			TypeDecl type = anchor.type();
			
			portsFeedback = new PortsFeedback(root, type);
			if (anchor.isSource()) {
				portsFeedback.showInPorts();
			} else {
				portsFeedback.showOutPorts();
			}
		}
		super.showSourceFeedback();
	}
	
	@Override
	public void eraseSourceFeedback() {
		hideFeedback();
		super.eraseSourceFeedback();
	}

	private void hideFeedback() {
		if (portsFeedback != null) {
			portsFeedback.hide();
			portsFeedback = null;
		}
	}
}
