package org.bloqqi.editor.wizards.features;

import org.bloqqi.compiler.ast.DiagramType;

public class FeatureWizardBlock extends WizardDiagramType {
	public FeatureWizardBlock(DiagramType diagramType, DiagramType enclosingDiagramType) {
		super(diagramType);
		getPageFeatures().setEnclosingDiagramType(enclosingDiagramType);
		setWindowTitle("Select features for new block");
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
