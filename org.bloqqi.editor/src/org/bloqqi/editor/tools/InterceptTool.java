package org.bloqqi.editor.tools;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.editor.editparts.ConnectionPart;
import org.bloqqi.editor.tools.CreateInterceptRequest.InterceptKind;

public class InterceptTool extends ConnectionCreationTool {
	public static final String REQ_INTERCEPT_FIRST = "intercept first";
	public static final String REQ_INTERCEPT_SECOND = "intercept second";

	private final Connection connection;
	private final EditPart rootPart;
	private final PortsFeedback portsFeedback;
	private final InterceptKind interceptKind;
	private boolean executed;

	public InterceptTool(ConnectionPart connectionPart, InterceptKind interceptKind) {
		setUnloadWhenFinished(true);
		this.interceptKind = interceptKind;
		rootPart = connectionPart.getRoot();
		connection = connectionPart.getModel();
		portsFeedback = new PortsFeedback(rootPart, connection.type());
		executed = false;
	}
	
	@Override
	public void activate() {
		super.activate();
		portsFeedback.showInPorts();
	}
	
	@Override
	public void deactivate() {
		// HACK! It otherwise causes a null pointer exception
		if (!executed) {
			portsFeedback.hide();
		}
		super.deactivate();
	}
	
	@Override
	protected void executeCommand(Command command) {
		executed = true;
		super.executeCommand(command);
	}
	
	@Override
	protected void setState(int state) {
		super.setState(state);
		if (state == STATE_CONNECTION_STARTED) {
			portsFeedback.showOutPorts();
		}
	}

	@Override
	protected Request createTargetRequest() {
		CreateInterceptRequest req = new CreateInterceptRequest();
		req.setFactory(getFactory());
		req.setConnection(connection);
		req.setInterceptKind(interceptKind);
		return req;
	}

	@Override
	protected String getCommandName() {
		if (isInState(STATE_CONNECTION_STARTED
				| STATE_ACCESSIBLE_DRAG_IN_PROGRESS)) {
			return REQ_INTERCEPT_SECOND;
		} else {
			return REQ_INTERCEPT_FIRST;
		}
	}
}
