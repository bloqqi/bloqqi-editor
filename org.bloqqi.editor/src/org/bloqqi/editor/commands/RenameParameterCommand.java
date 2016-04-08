package org.bloqqi.editor.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.List;
import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.PostConditionCommand;
	
public class RenameParameterCommand extends PostConditionCommand {
	private final Coordinates coordinates;
	private final Parameter parameter;
	private final Parameter declaredParameter;
	private final String oldName;
	private final String newName;
	private final DiagramType diagramType;
	
	private String errorMessage;
	
	public RenameParameterCommand(Coordinates coordinates, Parameter parameter, String newName) {
		this.coordinates = coordinates;
		this.parameter = parameter;
		this.declaredParameter = parameter.declaredParameter();
		this.oldName = parameter.name();
		this.newName = newName;
		this.diagramType = parameter.diagramType();
	}
	
	@Override
	public boolean canExecute() {
		return !parameter.isInherited();
	}
	
	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void execute() {
		String oldAccessString = parameter.accessString();
		if (declaredParameter.changeName(newName)) {
			declaredParameter.program().flushAllAttributes();
			updateCoordinates(oldAccessString);
			diagramType.notifyObservers();
		} else {
			errorMessage = "Could not change name to " + newName;
		}
	}

	@Override
	public void undo() {
		String oldAccessString = declaredParameter.accessString();
		if (declaredParameter.changeName(oldName)) {
			declaredParameter.program().flushAllAttributes();
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
		List<? extends Parameter> list;
		if (declaredParameter.isInParameter()) {
			list = forDiagramType.getInParameters();
		} else {
			list = forDiagramType.getOutParameterList();
		}
		Parameter inhPar = null;
		for (Parameter ip: list) {
			if (ip.declaredParameter() == declaredParameter) {
				inhPar = ip;
			}
		}
		Rectangle oldR = coordinates.getRectangle(forDiagramType, oldAccessString);
		coordinates.setRectangle(forDiagramType, inhPar.accessString(), oldR);
		coordinates.removeRectangle(forDiagramType, oldAccessString);
	}
}
