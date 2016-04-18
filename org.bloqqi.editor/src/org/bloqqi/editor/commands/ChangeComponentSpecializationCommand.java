package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;

import java.util.Set;

import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.SpecializeDiagramType;
import org.bloqqi.compiler.ast.VarUse;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class ChangeComponentSpecializationCommand extends Command {
	private Component component;
	private final DiagramType enclosingDiagramType;
	private final Set<NewInParameter> newInParameters;

	private boolean hasExecuted;
	
	public ChangeComponentSpecializationCommand(Component component,
			DiagramType enclosingDiagramType, SpecializeDiagramType specializeDt, 
			Set<NewInParameter> newInParameters) {
		this.component = component;
		this.enclosingDiagramType = enclosingDiagramType;
		this.newInParameters = newInParameters;
		hasExecuted = false;
	}
	
	public void execute() {
		// OK
		enclosingDiagramType.program().flushAllAttributes();

		if (!hasExecuted) {
			// Some parameters of nested components should be exposed as parameters.
			// This is set by the user in the specialization wizard.
			for (NewInParameter in: newInParameters) {
				String parameter = component.name() + "." + in.getName();
				Pair<Component, VarUse> p = enclosingDiagramType.addConnectionsParameters(parameter);
				component = p.first;
			}
		}
		
		enclosingDiagramType.program().flushAllAttributes();
		enclosingDiagramType.notifyObservers();
		
		hasExecuted = true;
	}

	@Override
	public boolean canUndo() {
		return true;
	}
	
	public void undo() {
		enclosingDiagramType.getLocalComponentList().removeChild(component);
		enclosingDiagramType.program().flushAllAttributes();
		enclosingDiagramType.notifyObservers();
	}
}
