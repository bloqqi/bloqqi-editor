package org.bloqqi.editor.commands;

import java.util.SortedMap;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.AnonymousDiagramType;
import org.bloqqi.compiler.ast.AnonymousType;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.editor.Coordinates;

public class DeleteDiagramTypeCommand extends Command {
	private final DiagramType diagramType;
	private final boolean isAnonymousType;
	private final CompilationUnit compUnit;

	// Only for ordinary diagram types
	private final int indexOfDiagramType;

	private final Coordinates coordinates;
	private final SortedMap<String, Rectangle> coordinatesMap;

	public DeleteDiagramTypeCommand(DiagramType diagramType, Coordinates coordinates) {
		this.diagramType = diagramType;
		this.isAnonymousType = diagramType.isAnonymousType();
		this.compUnit = diagramType.compUnit();
		if (diagramType.isAnonymousType()) {
			this.indexOfDiagramType = -1;
		} else {
			this.indexOfDiagramType = compUnit.getDeclarationList().getIndexOfChild(diagramType);
		}
		this.coordinates = coordinates;
		this.coordinatesMap = coordinates.getAllRectangles(diagramType);
	}

	@Override
	public boolean canExecute() {
		return true;
	}

	@Override
	public void execute() {
		coordinates.removeAllRectangles(diagramType);
		if (isAnonymousType) {
			TypeUse typeUse = diagramType.getSuperType(0).treeCopy();
			diagramType.enclosingBlock().setType(typeUse);
		} else {
			compUnit.getDeclarationList().removeChild(indexOfDiagramType);
		}
		compUnit.program().flushAllAttributes();
		compUnit.notifyObservers();
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		coordinates.setAllRectangles(diagramType, coordinatesMap);
		if (isAnonymousType) {
			diagramType.enclosingBlock().setType(new AnonymousType((AnonymousDiagramType) diagramType));
		} else {
			compUnit.getDeclarationList().insertChild(diagramType, indexOfDiagramType);
		}
		compUnit.program().flushAllAttributes();
		compUnit.notifyObservers();
	}
}
