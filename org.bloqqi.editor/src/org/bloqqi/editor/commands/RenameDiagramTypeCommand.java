package org.bloqqi.editor.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.bloqqi.compiler.ast.ASTNode;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.Coordinates;
import org.bloqqi.editor.commands.PostConditionCommand;

public class RenameDiagramTypeCommand extends PostConditionCommand {
	private final Coordinates coordinates;
	private final DiagramType diagramType;
	private final String oldName;
	private final String newName;

	private String errorMessage;
	
	public RenameDiagramTypeCommand(Coordinates coordinates, DiagramType diagramType, String newName) {
		this.coordinates = coordinates;
		this.diagramType = diagramType;
		this.oldName = diagramType.name();
		this.newName = newName;
	}
	
	@Override
	public boolean canExecute() {
		return true;
	}
	
	@Override
	public void execute() {
		if (diagramType.changeName(newName)) {
			diagramType.program().flushAllAttributes();
			updateCoordinates(oldName, newName);
			diagramType.compUnit().notifyObservers();
		} else {
			errorMessage = "Could not change name to " + newName;
		}
	}
	
	public void updateCoordinates(String oldName, String newName) {
		for (DiagramType dt: coordinates.getDiagramTypes()) {
			Map<String, Rectangle> map = coordinates.getAllRectangles(dt);
			
			Map<String, String> renameMap = new HashMap<>();
			for (Map.Entry<String, Rectangle> e: map.entrySet()) {
				String name = e.getKey();
				String split[] = name.split(ASTNode.DECLARED_IN_SEP);
				if (split.length >= 1) {
					String type = split[0];
					if (type.equals(oldName) || type.startsWith(oldName + ASTNode.INLINE_SEP)) {
						renameMap.put(name, name.replaceFirst(oldName, newName));
					}
				}
			}
			
			for (Map.Entry<String, String> rename: renameMap.entrySet()) {
				String oldEntryName = rename.getKey();
				String newEntryName = rename.getValue();
				Rectangle r = map.remove(oldEntryName);
				if (r != null) {
					map.put(newEntryName, r);
				}
			}
			
			coordinates.setAllRectangles(dt, map);
		}
	}

	public boolean canUndo() {
		return false;
	}
	
	@Override
	public boolean couldExecute() {
		return errorMessage == null;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
}
