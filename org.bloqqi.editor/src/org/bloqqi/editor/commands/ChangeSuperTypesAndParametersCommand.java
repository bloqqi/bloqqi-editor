package org.bloqqi.editor.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FlowDecl;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.InheritedConnection;
import org.bloqqi.compiler.ast.InheritedInParameter;
import org.bloqqi.compiler.ast.InheritedOutParameter;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.List;
import org.bloqqi.compiler.ast.Node;

public class ChangeSuperTypesAndParametersCommand extends Command {
	private final DiagramType diagramType;
	private final List<TypeUse> oldSuperTypes;
	private final List<InParameter> oldInParameters;
	private final List<OutParameter> oldOutParameters;
	private final List<FlowDecl> oldFlowDecls;
	
	private final Set<DiagramType> removedSuperTypes;
	private final ArrayList<InheritedInParameter> removedInParameters;
	private final ArrayList<InheritedOutParameter> removedOutParameters;
	
	private List<TypeUse> newSuperTypes;
	private List<InParameter> newInParameters;
	private List<OutParameter> newOutParameters;
	
	public ChangeSuperTypesAndParametersCommand(DiagramType diagramType) {
		this.diagramType = diagramType;
		oldSuperTypes = diagramType.getSuperTypes();
		oldInParameters = diagramType.getLocalInParameters();
		oldOutParameters = diagramType.getLocalOutParameters();
		oldFlowDecls = diagramType.getFlowDeclList().treeCopy();
		removedSuperTypes = new HashSet<>();
		removedInParameters = new ArrayList<>();
		removedOutParameters = new ArrayList<>();
	}
	
	public void setNewSuperTypes(java.util.List<String> superTypes) {
		newSuperTypes = new List<>();
		for (String name: superTypes) {
			newSuperTypes.add(new TypeUse(name));
		}
		
		Set<DiagramType> allNewSuperTypes = new HashSet<>();
		for (String superType: superTypes) {
			TypeDecl td = diagramType.program().lookupType(superType);
			if (td != null && td.isDiagramType()) {
				allNewSuperTypes.addAll(((DiagramType) td).superTypesLinearized());
			}
		}
		allNewSuperTypes.remove(diagramType); // may happen if there are cycles
		
		removedSuperTypes.addAll(diagramType.superTypesLinearized());
		removedSuperTypes.removeAll(allNewSuperTypes);
		removedSuperTypes.remove(diagramType);
	}
	public void setNewSuperTypesAsBefore() {
		newSuperTypes = diagramType.getSuperTypes().treeCopy();
	}
	
	public void setNewInParameters(java.util.List<InParameter> inParameters) {
		newInParameters = new List<>();
		for (InParameter in: inParameters) {
			newInParameters.add(in);
		}

		for (InheritedInParameter in: diagramType.inParameters()) {
			boolean isRemoved = true;
			for (InParameter newIn: inParameters) {
				if (newIn.name().equals(in.name())) {
					isRemoved = false;
				}
			}
			if (isRemoved && !in.isInherited()) {
				removedInParameters.add(in);
			}
		}
	}
	public void setNewInParametersAsBefore() {
		newInParameters = diagramType.getLocalInParameters().treeCopy();
	}
	
	public void setNewOutParameters(java.util.List<OutParameter> outParameters) {
		newOutParameters = new List<>();
		for (OutParameter out: outParameters) {
			newOutParameters.add(out);
		}
		
		for (InheritedOutParameter out: diagramType.outParameters()) {
			boolean isRemoved = true;
			for (OutParameter newOut: outParameters) {
				if (newOut.name().equals(out.name())) {
					isRemoved = false;
				}
			}
			if (isRemoved && !out.isInherited()) {
				removedOutParameters.add(out);
			}
		}
	}
	public void setNewOutParametersAsBefore() {
		newOutParameters = diagramType.getLocalOutParameters().treeCopy();
	}

	@Override
	public boolean canExecute() {
		return newSuperTypes != null;
	}
	
	@Override
	public void execute() {
		removeAffectedConnections();
		diagramType.setSuperTypeList(newSuperTypes);
		diagramType.setLocalInParameterList(newInParameters);
		diagramType.setLocalOutParameterList(newOutParameters);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}

	private void removeAffectedConnections() {
		// Step 1: identify connections to remove
		Set<FlowDecl> removeFlowDecls = new HashSet<>();
		if (!removedSuperTypes.isEmpty()) {
			for (FlowDecl fd: diagramType.getFlowDecls()) {
				if (connectedToRemovedSuperType(fd)) {
					removeFlowDecls.add(fd);
				}
			}
		}
		for (InheritedInParameter in: removedInParameters) {
			for (Connection c: in.outgoingConnections()) {
				removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
			}
		}
		for (InheritedOutParameter out: removedOutParameters) {
			for (Connection c: out.ingoingConnections()) {
				removeFlowDecls.add(((InheritedConnection) c).getDeclaredFlowDecl());
			}
		}
		
		// Step 2: remove connections
		for (FlowDecl fd: removeFlowDecls) {
			diagramType.getFlowDeclList().removeChild(fd);
		}
	}

	private boolean connectedToRemovedSuperType(FlowDecl fd) {
		for (Node node: fd.connectedNodes()) {
			if (removedSuperTypes.contains(node.declaredInDiagramType())) {
				return true;
			}
		}
		return false;
	}
	
	@Override 
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void undo() {
		diagramType.setFlowDeclList(oldFlowDecls);
		diagramType.setSuperTypeList(oldSuperTypes);
		diagramType.setLocalInParameterList(oldInParameters);
		diagramType.setLocalOutParameterList(oldOutParameters);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
}
