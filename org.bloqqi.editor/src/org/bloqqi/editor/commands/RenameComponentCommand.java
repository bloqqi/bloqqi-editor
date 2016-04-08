package org.bloqqi.editor.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedComponent;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.PostConditionCommand;

public class RenameComponentCommand extends PostConditionCommand {
	private final Coordinates coordinates;
	private final Component componentComputed;
	private final Component declaredComponent;
	private final DiagramType diagramType;
	private final String oldName;
	private final String newName;

	private String errorMessage;
	
	public RenameComponentCommand(Coordinates coordinates, Component component, String newName) {
		this.coordinates = coordinates;
		this.componentComputed = component;
		this.declaredComponent = component.declaredComponent();
		this.diagramType = component.diagramType();
		this.oldName = component.name();
		this.newName = newName;
	}
	
	@Override
	public boolean canExecute() {
		return !componentComputed.isInherited();
	}
	
	public boolean canUndo() {
		return true;
	}

	@Override
	public void execute() {
		String oldAccessString = declaredComponent.accessString();
		
		if (declaredComponent.changeName(newName)) {
			declaredComponent.program().flushAllAttributes();
			updateCoordinates(oldAccessString);
			diagramType.notifyObservers();
		} else {
			errorMessage = "Could not change name to " + newName;
		}
	}
	
	@Override
	public void undo() {
		String oldAccessString = declaredComponent.accessString();
		
		if (declaredComponent.changeName(oldName)) {
			declaredComponent.program().flushAllAttributes();
			updateCoordinates(oldAccessString);
			diagramType.notifyObservers();
		}
	}
	
	@Override
	public boolean couldExecute() {
		return errorMessage == null;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	
	
	/** 
	 * Update coordinates
	 */
	private void updateCoordinates(String oldAccessString) {
		updateCoordinate(diagramType, oldAccessString);
		for (DiagramType subType: diagramType.namedSubTypes()) {
			if (subType != diagramType) {
				updateCoordinateForSubType(subType, oldAccessString);
			}
		}
	}
	private void updateCoordinateForSubType(DiagramType subType, String oldAccessString) {
		updateCoordinate(subType, diagramType.name() + ASTNode.DECLARED_IN_SEP + oldAccessString);
	}
	private void updateCoordinate(DiagramType forDiagramType, String oldAccessString) {
		InheritedComponent inhComp = null;
		for (InheritedComponent ic: forDiagramType.getComponents()) {
			if (ic.getDeclaredComponent() == declaredComponent) {
				inhComp = ic;
			}
		}
		Rectangle oldR = coordinates.getRectangle(forDiagramType, oldAccessString);
		coordinates.setRectangle(forDiagramType, inhComp.accessString(), oldR);
		coordinates.removeRectangle(forDiagramType, oldAccessString);
	}
}
