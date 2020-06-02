package org.bloqqi.editor.wizards.features;


import org.eclipse.jface.wizard.Wizard;


import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FeatureSelection;

public class WizardDiagramType extends Wizard {
	private final PageFeatures pageDiagramType;
	private final FeatureSelection selection;
	protected String newName;

	public WizardDiagramType(DiagramType diagramType) {
		this(diagramType, diagramType.featureSelection());
	}

	public WizardDiagramType(DiagramType diagramType, FeatureSelection selection) {
		this.selection = selection;
		this.pageDiagramType = createPageFeatures();
		getPageFeatures().setFeatureSelection(selection);
		getPageFeatures().setDiagramType(diagramType);
		setHelpAvailable(false);
		setWindowTitle("Specialize diagram type");
	}

	protected PageFeatures createPageFeatures() {
		return new PageFeatures();
	}

	@Override
	public void addPages() {
		addPage(getPageFeatures());
	}

	@Override
	public boolean performFinish() {
		newName = getPageFeatures().getNewName();
		return true;
	}

	protected PageFeatures getPageFeatures() {
		return pageDiagramType;
	}

	public FeatureSelection getFeatureSelection() {
		return selection;
	}

	public String getNewName() {
		return newName;
	}
}
