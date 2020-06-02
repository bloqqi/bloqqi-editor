package org.bloqqi.editor.wizards.features;

import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;

public class FeatureWizardChangeBlock extends WizardDiagramType {
	public FeatureWizardChangeBlock(
			DiagramType diagramType,
			DiagramType enclosingDiagramType,
			Block block) {
		super(diagramType, diagramType.featureSelection(block.anonymousDiagramType()));
		getPageFeatures().setEnclosingDiagramType(enclosingDiagramType);
		getPageFeatures().setBlockName(block.name());
		setWindowTitle("Change features for existing block");
	}

	@Override
	protected PageFeatures createPageFeatures() {
		return new PageFeaturesChangeBlock();
	}

	@Override
	protected PageFeaturesChangeBlock getPageFeatures() {
		return (PageFeaturesChangeBlock) super.getPageFeatures();
	}
}
