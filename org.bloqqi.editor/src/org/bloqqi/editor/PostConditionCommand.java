package org.bloqqi.editor;

import org.eclipse.gef.commands.Command;

public abstract class PostConditionCommand extends Command {
	public abstract boolean couldExecute();
	public abstract String getErrorMessage();
}
