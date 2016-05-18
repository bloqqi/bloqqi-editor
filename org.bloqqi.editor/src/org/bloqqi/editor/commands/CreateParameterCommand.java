package org.bloqqi.editor.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;


import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.figures.ComponentFigure;

public class CreateParameterCommand extends Command {
	private final Point location;
	private final DiagramType diagramType;
	private boolean isInParameter;
	private String name;
	private String type;
	private Parameter parameter;
	private final Coordinates coordinates;

	public CreateParameterCommand(Point location, DiagramType diagramType, Coordinates coordinates) {
		this.location = location;
		this.diagramType = diagramType;
		this.coordinates = coordinates;
	}
	
	@Override
	public boolean canExecute() {
		return !nameExists(name);
	}
	
	@Override
	public void execute() {
		if (isInParameter) {
			parameter = new InParameter();
			diagramType.addLocalInParameter((InParameter) parameter);
		} else {
			parameter = new OutParameter();
			diagramType.addLocalOutParameter((OutParameter) parameter);
		}
		parameter.setID(name);
		parameter.setTypeUse(new TypeUse(type));
		diagramType.program().flushAllAttributes();
		coordinates.setRectangle(diagramType, parameter.accessString(), createRectangle());
		diagramType.notifyObservers();
	}

	@Override
	public boolean canUndo() {
		return true;
	}
	
	@Override
	public void undo() {
		if (isInParameter) {
			diagramType.getLocalInParameterList().removeChild(parameter);
		} else {
			diagramType.getLocalOutParameterList().removeChild(parameter);
		}
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}
	
	private Rectangle createRectangle() {
		Dimension dim = new Dimension(ComponentFigure.WIDTH, ComponentFigure.MIN_HEIGHT);
		return new Rectangle(location, dim);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setIsInParameter(boolean isInParameter) {
		this.isInParameter = isInParameter;
	}

	public boolean nameExists(String name) {
		return diagramType.lookup(name) != null;
	}
}
