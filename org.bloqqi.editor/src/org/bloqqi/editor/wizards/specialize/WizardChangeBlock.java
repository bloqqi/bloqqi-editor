package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Parameter;

public class WizardChangeBlock extends WizardDiagramType {
	public WizardChangeBlock(
			DiagramType diagramType,
			DiagramType enclosingDiagramType,
			Block block) {
		super(diagramType, diagramType.specialize(block.anonymousDiagramType()));
		getPageFeatures().setEnclosingDiagramType(enclosingDiagramType);
		getPageFeatures().setBlockName(block.name());
		setWindowTitle("Changing existing specialized block");
		
		for (String parPath: getFeatureConfiguration().getNewInParameters()) {
			Parameter par = block.anonymousDiagramType().isInnerParameterExposed(parPath);
			if (par != null) {
				getPageParameters().selectParameter(parPath, par);
			}
		}
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
