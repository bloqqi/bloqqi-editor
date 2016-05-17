package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedVariable;
import org.bloqqi.compiler.ast.Variable;

public class RenameVariableCommand extends RenameNodeCommand {
	private final Variable declaredVariable;

	public RenameVariableCommand(Variable variable) {
		super(variable.declaredVariable(),
				variable.name(),
				variable.diagramType(),
				variable.isInherited());
		this.declaredVariable = variable.declaredVariable();
	}
	
	protected String getNewAccessString(DiagramType forDiagramType) {
		InheritedVariable inhVar = null;
		for (InheritedVariable iv: forDiagramType.getVariables()) {
			if (iv.getDeclaredVariable() == declaredVariable) {
				inhVar = iv;
			}
		}
		return inhVar.accessString();
	}
}
