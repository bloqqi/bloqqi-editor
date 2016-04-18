package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.SpecializeDiagramType;

public class WizardChangeComponent extends WizardDiagramType {
	public WizardChangeComponent(DiagramType diagramType, DiagramType enclosingDiagramType,
			SpecializeDiagramType specializeDt) {
		super(diagramType, specializeDt);
		getPageDiagramType().setEnclosingDiagramType(enclosingDiagramType);
		getPageDiagramType().setComponentName("lol");
		setWindowTitle("Changing existing specialized component");
	}
	
	protected PageFeatures createPageDiagramType() {
		return new PageFeaturesChangeComponent();
	}
	
	protected PageFeaturesChangeComponent getPageDiagramType() {
		return (PageFeaturesChangeComponent) pageDiagramType;
	}
}
