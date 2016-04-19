package org.bloqqi.editor.wizards.specialize;


import org.eclipse.jface.wizard.Wizard;

import java.util.Set;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.SpecializeDiagramType;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class WizardDiagramType extends Wizard {
	private final PageFeatures pageDiagramType;
	private final SpecializeDiagramType specializeDt;
	private final PageParameters pageNewInParameters;
	protected String newName;
	protected Set<NewInParameter> newInParameters;
	
	public WizardDiagramType(DiagramType diagramType) {
		this(diagramType, diagramType.specialize());
	}

	public WizardDiagramType(DiagramType diagramType, SpecializeDiagramType specializeDt) {
		this.specializeDt = specializeDt;
		this.pageDiagramType = createPageFeatures();
		getPageFeatures().setSpecializeDiagramType(specializeDt);
		getPageFeatures().setDiagramType(diagramType);
		this.pageNewInParameters = new PageParameters(specializeDt);
		setHelpAvailable(false);
		setWindowTitle("Specialize diagram type");
	}
	
	protected PageFeatures createPageFeatures() {
		return new PageFeatures();
	}

	@Override
	public void addPages() {
		addPage(getPageFeatures());
		addPage(getPageParameters());
	}
	
	@Override
	public boolean performFinish() {
		newName = getPageFeatures().getNewName();
		newInParameters = getPageParameters().getNewInParameters();
		return true;
	}
	
	protected PageFeatures getPageFeatures() {
		return pageDiagramType;
	}

	protected PageParameters getPageParameters() {
		return pageNewInParameters;
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
