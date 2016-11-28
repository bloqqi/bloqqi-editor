package org.bloqqi.editor.commands;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Opt;
import org.bloqqi.compiler.ast.Pair;
import org.bloqqi.compiler.ast.Recommendation;
import org.bloqqi.compiler.ast.RecommendationBlock;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.WiredBlock;

public class ExtractSubTypeAsRecommendationCommand extends Command {
	private final CompilationUnit compUnit;
	private final DiagramType subType;
	private final DiagramType superType;
	private final String newDiagramTypeName;
	private final String newBlockName;

	private Recommendation recommendation;
	private DiagramType newDiagramType;
	
	public ExtractSubTypeAsRecommendationCommand(DiagramType subType, String newDiagramType, String newBlockName) {
		this.compUnit = subType.compUnit();
		this.subType = subType;
		this.newDiagramTypeName = newDiagramType;
		this.newBlockName = newBlockName;
		
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
		Pair<DiagramType, WiredBlock> p = subType.extractsubTypeAsWiredBlock(newDiagramTypeName, newBlockName);
		newDiagramType = p.first;
		compUnit.addDeclaration(newDiagramType);
		
		recommendation = new Recommendation();
		recommendation.setDeclaredForType(new TypeUse(superType.name()));
		recommendation.addRecommendationElement(new RecommendationBlock(p.second, new Opt<TypeUse>()));
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
