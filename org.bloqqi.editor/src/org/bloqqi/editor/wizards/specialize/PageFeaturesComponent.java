package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Program;

public class PageFeaturesComponent extends PageFeatures {
	protected DiagramType enclosingDiagramType;
	
	public void setEnclosingDiagramType(DiagramType enclosingDiagramType) {
		this.enclosingDiagramType = enclosingDiagramType;
	}
	
	@Override
	protected void createNewNameUI() {
		super.createNewNameUI();
		nameLabel.setText("Component name");
	}
	
	@Override
	protected boolean isNameValid(String name) {
		return Program.isIdValid(name) && enclosingDiagramType.lookup(name) == null;
	}
}
