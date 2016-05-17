package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedComponent;

public class RenameComponentCommand extends RenameNodeCommand {
	private final Component declaredComponent;

	public RenameComponentCommand(Component component) {
		super(component.declaredComponent(),
				component.name(),
				component.diagramType(),
				component.isInherited());
		this.declaredComponent = component.declaredComponent();
	}
	
	protected String getNewAccessString(DiagramType forDiagramType) {
		InheritedComponent inhComp = null;
		for (InheritedComponent ic: forDiagramType.getComponents()) {
			if (ic.getDeclaredComponent() == declaredComponent) {
				inhComp = ic;
			}
		}
		return inhComp.accessString();
	}
}
