package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;

import java.util.Set;

import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FeatureConfiguration;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class CreateDiagramTypeSpecializationCommand extends Command {
	private final CompilationUnit cu;
	private final DiagramType newDiagramType;
	private final Set<NewInParameter> newInParameters;
	private boolean hasExecuted;
	
	
	public CreateDiagramTypeSpecializationCommand(CompilationUnit cu,
			FeatureConfiguration conf, String newTypeName, Set<NewInParameter> newInParameters) {
		this.cu = cu;
		this.newInParameters = newInParameters;
		newDiagramType = conf.newDiagramType(newTypeName);
		hasExecuted = false;
	}
	
	public void execute() {
		cu.addDeclaration(newDiagramType);
		cu.program().flushAllAttributes();
		if (!hasExecuted) {
			for (NewInParameter in: newInParameters) {
				newDiagramType.addConnectionsParametersToDiagramType(in.getPath(), in.getNewName());
			}
		}
		cu.program().flushAllAttributes();
		cu.notifyObservers();
		hasExecuted = true;
	}
	
	public void undo() {
		cu.getDeclarationList().removeChild(newDiagramType);
		cu.program().flushAllAttributes();
		cu.notifyObservers();
	}
}
