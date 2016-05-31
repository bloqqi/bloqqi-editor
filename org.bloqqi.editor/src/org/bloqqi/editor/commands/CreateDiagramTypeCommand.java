package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Modifiers;
import org.bloqqi.compiler.ast.Program;

public class CreateDiagramTypeCommand extends Command {
	private final String newName;
	private final Program program;
	private final boolean canUndo;
	private final DiagramType newDiagramType;

	public CreateDiagramTypeCommand(Program program, String newName) {
		this(program, newName, true);
	}
	
	public CreateDiagramTypeCommand(Program program, String newName, boolean canUndo) {
		this.program = program;
		this.newName = newName;
		this.canUndo = canUndo;
		newDiagramType = new DiagramType();
		newDiagramType.setID(newName);
		newDiagramType.setModifiers(new Modifiers());
	}

	@Override
	public boolean canExecute() {
		return !program.isTypeDeclared(newName);
	}
	
	@Override
	public boolean canUndo() {
		return canUndo;
	}

	@Override
	public void execute() {
		program.getCompilationUnit(0).addDeclaration(newDiagramType);
		program.flushAllAttributes();
		program.getCompilationUnit(0).notifyObservers();
	}
	
	@Override
	public void undo() {
		program.getCompilationUnit(0).getDeclarationList().removeChild(newDiagramType);
		program.flushAllAttributes();
		program.getCompilationUnit(0).notifyObservers();
	}
	
	public DiagramType getNewDiagramType() {
		return newDiagramType;
	}
}
