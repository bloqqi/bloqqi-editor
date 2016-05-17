package org.bloqqi.editor.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import java.util.HashSet;
import java.util.Set;

import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.IdName;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.VarUse;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.editparts.ComponentParameterPart;
import org.bloqqi.editor.figures.ComponentFigure;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class CreateComponentCommand extends Command {
	private final Point location;
	private Component component;
	private final DiagramType diagramType;
	private final Coordinates coordinates;
	private Set<NewInParameter> newInParameters;

	private boolean hasExecuted;
	
	public CreateComponentCommand(Point location, Component component, DiagramType diagramType, Coordinates coordinates) {
		this.location = location;
		this.component = component;
		this.diagramType = diagramType;
		this.coordinates = coordinates;
		setNewInParameters(new HashSet<>());
		hasExecuted = false;
	}
	
	public void execute() {
		if (component.getName() == null
				|| !Program.isIdValid(component.name())
				|| diagramType.lookup(component.name()) != null) {
			setSimpleName(computeNewName());
		}
		diagramType.addLocalComponent(component);
		diagramType.program().flushAllAttributes();

		if (!hasExecuted) {
			// Some parameters of nested components should be exposed as parameters.
			// This is set by the user in the specialization wizard.
			for (NewInParameter in: newInParameters) {
				String parameter = component.name() + "." + in.getPath();
				Pair<Component, VarUse> p = diagramType.addConnectionsParameters(parameter, in.getNewName());
				component = p.first;
			}
		}
		
		diagramType.program().flushAllAttributes();
		coordinates.setRectangle(diagramType, component.accessString(), createRectangle());
		diagramType.notifyObservers();
		
		hasExecuted = true;
	}

	private Rectangle createRectangle() {
		int ports = Math.max(component.getNumInParameter(), component.getNumOutParameter());
		int height = ports * (ComponentParameterPart.SIZE + ComponentParameterPart.PADDING);
		Dimension dim = new Dimension(ComponentFigure.WIDTH, Math.max(ComponentFigure.MIN_HEIGHT, height));
		return new Rectangle(location, dim);
	}
	
	public void setName(String name) {
		component.setName(new IdName(name));
		component.setHasSimpleName(false);
	}
	
	public void setSimpleName(String name) {
		component.setName(new IdName(name));
		component.setHasSimpleName(true);
	}
	
	public String computeNewName() {
		int i = 0;
		String name;
		do {
			i++;
			name = component.getType().name() + "_" + i;
		} while(diagramType.lookup(name) != null);
		return name;
	}

	@Override
	public boolean canUndo() {
		return true;
	}
	
	public void undo() {
		diagramType.getLocalComponentList().removeChild(component);
		diagramType.program().flushAllAttributes();
		diagramType.notifyObservers();
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public void setNewInParameters(Set<NewInParameter> newInParameters) {
		this.newInParameters = newInParameters;
	}
}
