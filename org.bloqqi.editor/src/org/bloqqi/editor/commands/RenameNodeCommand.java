package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Node;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.PostConditionCommand;
import org.eclipse.draw2d.geometry.Rectangle;

public abstract class RenameNodeCommand extends PostConditionCommand {
	protected final Node declaredNode;
	protected final String oldName;
	protected final DiagramType diagramType;
	protected final boolean isInherited;
	
	protected Coordinates coordinates;
	protected String newName;
	protected String errorMessage;

	public RenameNodeCommand(Node declaredNode, String oldName, DiagramType diagramType, boolean isInherited) {
		this.declaredNode = declaredNode;
		this.oldName = oldName;
		this.diagramType = diagramType;
		this.isInherited = isInherited;
		errorMessage = null;
	}
	
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	public void setNewName(String newName) {
		this.newName = newName;
	}
	
	@Override
	public boolean canExecute() {
		return !isInherited;
	}
	
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void execute() {
		String oldAccessString = declaredNode.accessString();
		if (declaredNode.changeName(newName)) {
			diagramType.program().flushAllAttributes();
			updateCoordinates(oldAccessString);
			diagramType.notifyObservers();
		} else {
			errorMessage = "Could not change name to " + newName;
		}
	}
	
	@Override
	public void undo() {
		String oldAccessString = declaredNode.accessString();
		if (declaredNode.changeName(oldName)) {
			diagramType.program().flushAllAttributes();
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
	 * Updating coordinates database
	 */
	protected void updateCoordinates(String oldAccessString) {
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
		String accessString = getNewAccessString(forDiagramType);
		Rectangle oldR = coordinates.getRectangle(forDiagramType, oldAccessString);
		coordinates.setRectangle(forDiagramType, accessString, oldR);
		coordinates.removeRectangle(forDiagramType, oldAccessString);
	}

	abstract protected String getNewAccessString(DiagramType forDiagramType);
}
