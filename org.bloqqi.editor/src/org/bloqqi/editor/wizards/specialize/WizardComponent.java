package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.DiagramType;

public class WizardComponent extends WizardDiagramType {
	public WizardComponent(DiagramType diagramType, DiagramType enclosingDiagramType) {
		super(diagramType);
		getPageDiagramType().setEnclosingDiagramType(enclosingDiagramType);
		setWindowTitle("Create new specialized component");
	}
	
	protected PageFeatures createPageDiagramType() {
		return new PageFeaturesNewComponent();
	}
	
	protected PageFeaturesNewComponent getPageDiagramType() {
		return (PageFeaturesNewComponent) pageDiagramType;
	}
}
