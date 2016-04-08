package org.bloqqi.editor.tools;

import org.eclipse.gef.requests.CreateConnectionRequest;

import org.bloqqi.compiler.ast.Connection;


public class CreateInterceptRequest extends CreateConnectionRequest {
	private Connection connection;
	private InterceptKind interceptKind;
	
	public enum InterceptKind {
		CONNECTION,
		SOURCE
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public InterceptKind getInterceptKind() {
		return interceptKind;
	}
	
	public void setInterceptKind(InterceptKind interceptKind) {
		this.interceptKind = interceptKind;
	}
}
