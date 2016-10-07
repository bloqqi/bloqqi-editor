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
	private final DiagramType diagramType;
	private final Coordinates coordinates;

	private Variable variable;
	private String name;
	private TypeUse typeUse;
	
	public CreateVariableCommand(Point location, DiagramType diagramType, Coordinates coordinates) {
		this.location = location;
		this.diagramType = diagramType;
		this.coordinates = coordinates;
	}
	
	@Override
	public boolean canExecute() {
		return variable != null && !nameExists(variable.name());
	}
	
	@Override
	public void execute() {
		variable.setID(name);
		variable.setTypeUse(typeUse);

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
	
	public void setVariable(Variable variable) {
		this.variable = variable;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.typeUse = new TypeUse(type);
	}

	public boolean nameExists(String name) {
		return diagramType.lookup(name) != null;
	}
}
