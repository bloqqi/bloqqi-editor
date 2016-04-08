package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.Recommendation;
import org.bloqqi.compiler.ast.RecommendationComponent;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.WiredComponent;

public class ExtractSubTypeAsRecommendationCommand extends Command {
	private final CompilationUnit compUnit;
	private final DiagramType subType;
	private final DiagramType superType;
	private final String newDiagramTypeName;
	private final String newComponentName;

	private Recommendation recommendation;
	private DiagramType newDiagramType;
	
	public ExtractSubTypeAsRecommendationCommand(DiagramType subType, String newDiagramType, String newComponentName) {
		this.compUnit = subType.compUnit();
		this.subType = subType;
		this.newDiagramTypeName = newDiagramType;
		this.newComponentName = newComponentName;
		
		// Require that there is only one super type
		if (subType.directSuperTypes().size() == 1) {
			this.superType = subType.directSuperTypes().iterator().next();
		} else {
			this.superType = null;
		}
	}
	
	@Override
	public boolean canExecute() {
		return superType != null;
	}
	
	@Override
	public void execute() {
		Pair<DiagramType, WiredComponent> p = subType.extractsubTypeAsWiredBlock(newDiagramTypeName, newComponentName);
		newDiagramType = p.first;
		compUnit.addDeclaration(newDiagramType);
		
		recommendation = new Recommendation();
		recommendation.setDeclaredForType(new TypeUse(superType.name()));
		recommendation.addRecommendationElement(new RecommendationComponent(p.second));
		compUnit.addDeclaration(recommendation);
		
		compUnit.program().flushAllAttributes();
		compUnit.notifyObservers();
	}
	
	@Override
	public void undo() {
		compUnit.getDeclarationList().removeChild(recommendation);
		compUnit.getDeclarationList().removeChild(newDiagramType);
		
		compUnit.program().flushAllAttributes();
		compUnit.notifyObservers();
	}
}
