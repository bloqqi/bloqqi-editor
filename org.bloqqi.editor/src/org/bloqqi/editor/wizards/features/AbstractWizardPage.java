package org.bloqqi.editor.wizards.features;

import org.eclipse.jface.wizard.WizardPage;

public abstract class AbstractWizardPage extends WizardPage {
	protected AbstractWizardPage(String pageName) {
		super(pageName);
	}

	public void firePageChanged() {
	}
}
