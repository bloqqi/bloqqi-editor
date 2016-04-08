package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.Interception;
import org.bloqqi.compiler.ast.SourceInterception;

public class SourceInterceptCommand extends AbstractInterceptCommand {
	@Override
	protected Interception createInterceptionDecl() {
		Interception i = new SourceInterception();
		i.setIntercepted(connection.getSource().anchor().access());
		i.setSource(source.access());
		i.setTarget(target.access());
		return i;
	}

}
