package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.ConnectionInterception;
import org.bloqqi.compiler.ast.Interception;

public class TargetInterceptCommand extends AbstractInterceptCommand {
	@Override
	protected Interception createInterceptionDecl() {
		Interception i = new ConnectionInterception();
		i.setIntercepted(connection.getTarget().anchor().access());
		i.setSource(source.access());
		i.setTarget(target.access());
		return i;
	}
}
