package org.bloqqi.editor.wizards.specialize;


import org.eclipse.jface.wizard.Wizard;

import java.util.Set;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FeatureConfiguration;
import org.bloqqi.editor.wizards.specialize.PageParameters.NewInParameter;

public class WizardDiagramType extends Wizard {
	private final PageFeatures pageDiagramType;
	private final FeatureConfiguration conf;
	private final PageParameters pageNewInParameters;
	protected String newName;
	protected Set<NewInParameter> newInParameters;
	
	public WizardDiagramType(DiagramType diagramType) {
		this(diagramType, diagramType.specialize());
	}

	public WizardDiagramType(DiagramType diagramType, FeatureConfiguration conf) {
		this.conf = conf;
		this.pageDiagramType = createPageFeatures();
		getPageFeatures().setFeatureConfiguration(conf);
		getPageFeatures().setDiagramType(diagramType);
		this.pageNewInParameters = new PageParameters(conf);
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
	
	public FeatureConfiguration getFeatureConfiguration() {
		return conf;
	}
	
	public String getNewName() {
		return newName;
	}
	
	public Set<NewInParameter> getNewInParameters() {
		return newInParameters;
	}
}
