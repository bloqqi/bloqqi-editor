package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.DiagramType;

public class WizardComponent extends WizardDiagramType {
	public WizardComponent(DiagramType diagramType, DiagramType enclosingDiagramType) {
		super(diagramType);
		getPageFeatures().setEnclosingDiagramType(enclosingDiagramType);
		setWindowTitle("Create new specialized component");
	}
	
	@Override
	protected PageFeatures createPageFeatures() {
		return new PageFeaturesNewComponent();
	}
	
	@Override
	protected PageFeaturesNewComponent getPageFeatures() {
		return (PageFeaturesNewComponent) super.getPageFeatures();
	}
}
