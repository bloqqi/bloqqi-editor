package org.bloqqi.editor.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;


import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.Variable;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.figures.ComponentFigure;

public class CreateVariableCommand extends Command {
	private final Point location;
	private final Variable variable;
	private final DiagramType diagramType;
	private final Coordinates coordinates;

	public CreateVariableCommand(Point location, Variable variable, DiagramType diagramType, Coordinates coordinates) {
		this.location = location;
		this.variable = variable;
		this.diagramType = diagramType;
		this.coordinates = coordinates;
	}
	
	@Override
	public boolean canExecute() {
		return !nameExists(variable.name());
	}
	
	@Override
	public void execute() {
		diagramType.addLocalVariable(variable);
		diagramType.program().flushAllAttributes();
		coordinates.setRectangle(diagramType, variable.accessString(), createRectangle());
		diagramType.notifyObservers();
	}

	@Override
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void undo() {
		diagramType.getLocalVariableList().removeChild(variable);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
	
	private Rectangle createRectangle() {
		Dimension dim = new Dimension(ComponentFigure.WIDTH, ComponentFigure.MIN_HEIGHT);
		return new Rectangle(location, dim);
	}
	
	public void setName(String name) {
		variable.setID(name);
	}
	
	public void setType(String type) {
		variable.setTypeUse(new TypeUse(type));
	}

	public boolean nameExists(String name) {
		return diagramType.lookup(name) != null;
	}
}
