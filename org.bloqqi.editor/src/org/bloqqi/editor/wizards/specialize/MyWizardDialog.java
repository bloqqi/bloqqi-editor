package org.bloqqi.editor.wizards.specialize;

import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class MyWizardDialog extends WizardDialog {
	public MyWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	protected void firePageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() instanceof AbstractWizardPage) {
			((AbstractWizardPage) event.getSelectedPage()).firePageChanged();
		}
	}
}
