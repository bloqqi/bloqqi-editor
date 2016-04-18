package org.bloqqi.editor.wizards.specialize;


import org.eclipse.jface.wizard.Wizard;

import java.util.Set;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.SpecializeDiagramType;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class WizardDiagramType extends Wizard {
	protected final PageFeatures pageDiagramType;
	protected final SpecializeDiagramType specializeDt;
	protected final PageParameters pageNewInParameters;
	protected String newName;
	protected Set<NewInParameter> newInParameters;
	
	public WizardDiagramType(DiagramType diagramType) {
		this(diagramType, diagramType.specialize());
	}

	public WizardDiagramType(DiagramType diagramType, SpecializeDiagramType specializeDt) {
		this.specializeDt = specializeDt;
		pageDiagramType = createPageDiagramType();
		pageDiagramType.setSpecializeDiagramType(specializeDt);
		pageDiagramType.setDiagramType(diagramType);
		pageNewInParameters = new PageParameters(specializeDt);
		setHelpAvailable(false);
		setWindowTitle("Specialize diagram type");
	}
	
	protected PageFeatures createPageDiagramType() {
		return new PageFeatures();
	}

	@Override
	public void addPages() {
		addPage(pageDiagramType);
		addPage(pageNewInParameters);
	}
	
	@Override
	public boolean performFinish() {
		newName = pageDiagramType.getNewName();
		newInParameters = pageNewInParameters.getNewInParameters();
		return true;
	}
	
	public SpecializeDiagramType getConfiguration() {
		return specializeDt;
	}
	
	public String getNewName() {
		return newName;
	}
	
	public Set<NewInParameter> getNewInParameters() {
		return newInParameters;
	}
}
