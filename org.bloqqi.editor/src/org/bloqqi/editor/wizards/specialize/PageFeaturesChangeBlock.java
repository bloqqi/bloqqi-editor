package org.bloqqi.editor.wizards.specialize;

import org.bloqqi.compiler.ast.DiagramType;
import org.eclipse.swt.widgets.Composite;

public class PageFeaturesChangeBlock extends PageFeatures {
	protected DiagramType enclosingDiagramType;
	protected String blockName;
	
	public void setEnclosingDiagramType(DiagramType enclosingDiagramType) {
		this.enclosingDiagramType = enclosingDiagramType;
	}
	
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	
	@Override
	protected void createNewNameUI() {
		super.createNewNameUI();
		nameLabel.setText("Block name");
		newNameText.setText(blockName);
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
