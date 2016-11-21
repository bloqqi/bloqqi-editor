package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.DiagramType;

public class WizardBlock extends WizardDiagramType {
	public WizardBlock(DiagramType diagramType, DiagramType enclosingDiagramType) {
		super(diagramType);
		getPageFeatures().setEnclosingDiagramType(enclosingDiagramType);
		setWindowTitle("Create new specialized block");
	}
	
	@Override
	protected PageFeatures createPageFeatures() {
		return new PageFeaturesNewBlock();
	}
	
	@Override
	protected PageFeaturesNewBlock getPageFeatures() {
		return (PageFeaturesNewBlock) super.getPageFeatures();
	}
}
