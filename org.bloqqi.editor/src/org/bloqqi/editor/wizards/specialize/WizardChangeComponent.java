package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Parameter;

public class WizardChangeComponent extends WizardDiagramType {
	public WizardChangeComponent(
			DiagramType diagramType,
			DiagramType enclosingDiagramType,
			Component component) {
		super(diagramType, diagramType.specialize(component.anonymousDiagramType()));
		getPageFeatures().setEnclosingDiagramType(enclosingDiagramType);
		getPageFeatures().setComponentName(component.name());
		setWindowTitle("Changing existing specialized component");
		
		for (String parPath: getConfiguration().getNewInParameters()) {
			Parameter par = component.anonymousDiagramType().isInnerParameterExposed(parPath);
			if (par != null) {
				getPageParameters().selectParameter(parPath, par);
			}
		}
	}
	
	@Override
	protected PageFeatures createPageFeatures() {
		return new PageFeaturesChangeComponent();
	}
	
	@Override
	protected PageFeaturesChangeComponent getPageFeatures() {
		return (PageFeaturesChangeComponent) super.getPageFeatures();
	}
}
