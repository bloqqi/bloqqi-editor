package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bloqqi.compiler.ast.Anchor;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FeatureConfiguration;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.VarUse;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class ChangeComponentSpecializationCommand extends Command {
	private final Component oldComponent;
	private final int componentIndex;
	private final FeatureConfiguration conf;
	private final DiagramType enclosingDt;
	private final Set<NewInParameter> newInParameters;

	private Component newComponent;
	
	private boolean hasExecuted;
	private Map<FlowDecl, Integer> removeFlowDecls;
	
	public ChangeComponentSpecializationCommand(Component existingComponent,
			FeatureConfiguration conf,
			Set<NewInParameter> newInParameters) {
		this.oldComponent = existingComponent;
		this.conf = conf;
		this.enclosingDt = existingComponent.diagramType();
		this.componentIndex = enclosingDt.getLocalComponents().getIndexOfChild(existingComponent);
		this.newInParameters = newInParameters;
		hasExecuted = false;
	}
	
	public void execute() {
		this.newComponent = oldComponent.treeCopy();
		this.newComponent.setType(conf.newAnonymousComponent("").getType());

		enclosingDt.getLocalComponentList().setChild(newComponent, componentIndex);
		enclosingDt.program().flushAllAttributes();
		
		if (!hasExecuted) {
			// Some parameters of nested components should be exposed as parameters.
			// This is set by the user in the specialization wizard.
			for (NewInParameter in: newInParameters) {
				String parameter = oldComponent.name() + "." + in.getPath();
				Pair<Component, VarUse> p = enclosingDt.addConnectionsParameters(parameter, in.getNewName());
				newComponent = p.first;
			}
		}
		enclosingDt.program().flushAllAttributes();
		
		removeAffectedConnections();
		enclosingDt.program().flushAllAttributes();

		enclosingDt.notifyObservers();
		
		hasExecuted = true;
	}

	private void removeAffectedConnections() {
		removeFlowDecls = new HashMap<>();
		for (FlowDecl fd: enclosingDt.getFlowDecls()) {
			for (Pair<Node, Anchor> p: fd.connectedNodesAnchors()) {
				if (p.first == newComponent && p.second == null) {
					removeFlowDecls.put(fd, enclosingDt.getFlowDecls().getIndexOfChild(fd));
					break;
				}
			}
		}
		
		for (FlowDecl fd: removeFlowDecls.keySet()) {
			enclosingDt.getFlowDecls().removeChild(fd);
		}
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}
	
	public void undo() {
		enclosingDt.getLocalComponentList().setChild(oldComponent, componentIndex);
		for (Map.Entry<FlowDecl, Integer> e: removeFlowDecls.entrySet()) {
			enclosingDt.getFlowDecls().insertChild(e.getKey(), e.getValue());
		}
		enclosingDt.program().flushAllAttributes();
		enclosingDt.notifyObservers();
	}
}
