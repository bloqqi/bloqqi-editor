package org.bloqqi.editor.commands;

import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.Utils;

public class AutoLayoutCommand extends Command {
	private final DiagramType diagramType;
	private final Coordinates coordinates;
	
	private Map<String, Rectangle> oldCoordinatesMap;
	
	public AutoLayoutCommand(DiagramType diagramType, Coordinates coordinates) {
		this.diagramType = diagramType;
		this.coordinates = coordinates;
	}

	@Override
	public void execute() {
		oldCoordinatesMap = coordinates.getAllRectangles(diagramType);
		Map<String, Rectangle> newCoordinates = Utils.autoLayout(diagramType);
		coordinates.setAllRectangles(diagramType, newCoordinates);
		diagramType.notifyObservers();
	}
	
	@Override
	public void undo() {
		coordinates.setAllRectangles(diagramType, oldCoordinatesMap);
		diagramType.notifyObservers();
	}
}
