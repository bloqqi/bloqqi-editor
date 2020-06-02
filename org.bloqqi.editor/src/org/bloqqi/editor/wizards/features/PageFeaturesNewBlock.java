package org.bloqqi.editor.wizards.features;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Program;

public class PageFeaturesNewBlock extends PageFeatures {
	protected DiagramType enclosingDiagramType;

	public void setEnclosingDiagramType(DiagramType enclosingDiagramType) {
		this.enclosingDiagramType = enclosingDiagramType;
	}

	@Override
	protected void createNewNameUI() {
		super.createNewNameUI();
		nameLabel.setText("Block name");
	}

	@Override
	protected boolean isNameValid(String name) {
		return Program.isIdValid(name) && enclosingDiagramType.lookup(name) == null;
	}
}
