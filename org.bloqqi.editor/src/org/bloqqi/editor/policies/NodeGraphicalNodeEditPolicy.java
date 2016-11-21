package org.bloqqi.editor.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.editor.commands.AbstractInterceptCommand;
import org.bloqqi.editor.commands.CreateConnectionCommand;
import org.bloqqi.editor.commands.SourceInterceptCommand;
import org.bloqqi.editor.commands.TargetInterceptCommand;
import org.bloqqi.editor.tools.CreateInterceptRequest;
import org.bloqqi.editor.tools.InterceptTool;
import org.bloqqi.editor.tools.CreateInterceptRequest.InterceptKind;

public class NodeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {
	@Override
	public Command getCommand(Request request) {
		if (InterceptTool.REQ_INTERCEPT_FIRST.equals(request.getType())) {
			return getInterceptFirstCommand((CreateInterceptRequest) request);
		} else if (InterceptTool.REQ_INTERCEPT_SECOND.equals(request.getType())) {
			return getInterceptSecondCommand((CreateInterceptRequest) request);
		}
		return super.getCommand(request);
	}
	
	@Override
	public EditPart getTargetEditPart(Request request) {
		if (InterceptTool.REQ_INTERCEPT_FIRST.equals(request.getType())
				|| InterceptTool.REQ_INTERCEPT_SECOND.equals(request.getType())) {
			return getHost();
		}
		return super.getTargetEditPart(request);
	}

	protected Command getInterceptFirstCommand(CreateInterceptRequest request) {
		AbstractInterceptCommand cmd = null;
		Anchor anchor = (Anchor) getHost().getModel();
		if (canAccess(anchor) && anchor.node().isBlock()) {
			if (request.getInterceptKind() == InterceptKind.CONNECTION) {
				cmd = new TargetInterceptCommand();
			} else {
				cmd = new SourceInterceptCommand();
			}
			cmd.setTarget(anchor);
			cmd.setConnection(request.getConnection());
			request.setStartCommand(cmd);
		}
		return cmd;
	}

	protected Command getInterceptSecondCommand(CreateInterceptRequest request) {
		AbstractInterceptCommand cmd = null;
		Anchor anchor = (Anchor) getHost().getModel();
		if (canAccess(anchor) && anchor.node().isBlock()) {
			cmd = (AbstractInterceptCommand) request.getStartCommand();
			cmd.setSource(anchor);
		}
		return cmd;
	}

	
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		CreateConnectionCommand cmd = null;
		Anchor anchor = (Anchor) getHost().getModel();
		if (canAccess(anchor) && !anchor.node().isOutParameter()) {
			cmd = new CreateConnectionCommand();
			cmd.setSource(anchor);
			cmd.setConnection((Connection) request.getNewObject());
			request.setStartCommand(cmd);
		}
		return cmd;
	}
	
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		CreateConnectionCommand cmd = null;
		Anchor anchor = (Anchor) getHost().getModel();
		if (canAccess(anchor) && !anchor.node().isInParameter()) {
			cmd = (CreateConnectionCommand) request.getStartCommand();
			cmd.setTarget(anchor);
		}
		return cmd;
	}

	private boolean canAccess(Anchor anchor) {
		return anchor.canAccess() || anchor.canModifyToAccess();
	}
	

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}
}
