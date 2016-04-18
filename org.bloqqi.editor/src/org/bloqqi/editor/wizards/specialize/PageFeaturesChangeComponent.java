package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.DiagramType;
import org.eclipse.swt.widgets.Composite;

public class PageFeaturesChangeComponent extends PageFeatures {
	protected DiagramType enclosingDiagramType;
	protected String componentName;
	
	public void setEnclosingDiagramType(DiagramType enclosingDiagramType) {
		this.enclosingDiagramType = enclosingDiagramType;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	@Override
	protected void createNewNameUI() {
		super.createNewNameUI();
		nameLabel.setText("Component name");
		newNameText.setText(componentName);
		newNameText.setEnabled(false);
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		setPageComplete(true);
	}
	
	@Override
	protected boolean isNameValid(String name) {
		return true;
	}
}
