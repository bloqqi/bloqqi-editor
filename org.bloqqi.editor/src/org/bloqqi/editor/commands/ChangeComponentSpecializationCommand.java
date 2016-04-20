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
	private final Component existingComponent;
	private final int componentIndex;
	private final DiagramType enclosingDt;
	private final Set<NewInParameter> newInParameters;

	private Component newComponent;
	
	private boolean hasExecuted;
	
	public ChangeComponentSpecializationCommand(Component existingComponent,
			SpecializeDiagramType specializeDt,
			Set<NewInParameter> newInParameters) {
		this.existingComponent = existingComponent;
		this.enclosingDt = existingComponent.diagramType();
		this.componentIndex = enclosingDt.getLocalComponents().getIndexOfChild(existingComponent);
		this.newInParameters = newInParameters;
		this.newComponent = existingComponent.treeCopy();
		this.newComponent.setType(specializeDt.newAnonymousComponent("").getType());
		hasExecuted = false;
	}
	
	public void execute() {
		enclosingDt.getLocalComponentList().setChild(newComponent, componentIndex);
		enclosingDt.program().flushAllAttributes();

		if (!hasExecuted) {
			// Some parameters of nested components should be exposed as parameters.
			// This is set by the user in the specialization wizard.
			for (NewInParameter in: newInParameters) {
				String parameter = existingComponent.name() + "." + in.getPath();
				Pair<Component, VarUse> p = enclosingDt.addConnectionsParameters(parameter, in.getNewName());
				newComponent = p.first;
			}
		}
		
		enclosingDt.program().flushAllAttributes();
		enclosingDt.notifyObservers();
		
		hasExecuted = true;
	}

	@Override
	public boolean canUndo() {
		return true;
	}
	
	public void undo() {
		enclosingDt.getLocalComponentList().setChild(existingComponent, componentIndex);
		enclosingDt.program().flushAllAttributes();
		enclosingDt.notifyObservers();
	}
}
