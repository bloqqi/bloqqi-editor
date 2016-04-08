package org.bloqqi.editor.wizards.specialize;

import org.eclipse.jface.wizard.WizardPage;

public abstract class AbstractWizardPage extends WizardPage {
	protected AbstractWizardPage(String pageName) {
		super(pageName);
	}
	
	public void firePageChanged() {
	}
}
