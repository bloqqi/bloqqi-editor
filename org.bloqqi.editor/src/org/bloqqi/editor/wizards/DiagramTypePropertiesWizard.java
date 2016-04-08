package org.bloqqi.editor.wizards;


import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.OutParameter;

public class DiagramTypePropertiesWizard extends Wizard {
	private final DiagramTypePropertiesPage page;

	private List<String> superTypes;
	private List<InParameter> inParameters;
	private List<OutParameter> outParameters;
	
	public DiagramTypePropertiesWizard(DiagramType diagramType) {
		page = new DiagramTypePropertiesPage(diagramType);
		setHelpAvailable(false);
		setWindowTitle("Diagram type properties");
	}

	@Override
	public void addPages() {
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		superTypes = page.getSuperTypes();
		inParameters = page.getInParameters();
		outParameters = page.getOutParameters();
		return true;
	}
	
	public List<String> getNewSuperTypes() {
		return superTypes;
	}
	
	public List<InParameter> getNewInParameters() {
		return inParameters;
	}
	
	public List<OutParameter> getNewOutParameters() {
		return outParameters;
	}
}
