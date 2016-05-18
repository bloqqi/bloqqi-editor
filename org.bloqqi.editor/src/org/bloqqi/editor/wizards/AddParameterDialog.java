package org.bloqqi.editor.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AddParameterDialog extends AddVariableDialog {
	protected Combo parameterKindCombo;
	protected boolean inParameter;
	
	public AddParameterDialog(Shell parentShell) {
		super(parentShell);
	}

	public boolean isInParameter() {
		return inParameter;
	}
	
	@Override
	protected void okPressed() {
		String selectedKind = parameterKindCombo.getItem(parameterKindCombo.getSelectionIndex());
		inParameter = selectedKind.equals("Input");
		super.okPressed();
	}
	
	@Override
	protected void createFields(Composite container) {
		new Label(container, SWT.NONE).setText("Parameter kind");
		parameterKindCombo = new Combo (container, SWT.READ_ONLY);
		parameterKindCombo.setItems (new String [] {"Input", "Output"});
		parameterKindCombo.select(0);
		parameterKindCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		super.createFields(container);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add parameter");
	}
}
