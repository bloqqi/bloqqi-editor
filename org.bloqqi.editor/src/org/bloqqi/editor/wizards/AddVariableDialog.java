package org.bloqqi.editor.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class AddVariableDialog extends AbstractAddNameDialog {
	public static final String KIND_STATE = "state";
	public static final String KIND_INPUT = "input";
	public static final String KIND_OUTPUT = "output";
	
	protected Combo kindCombo;
	protected String kind;
	
	public AddVariableDialog(Shell parentShell) {
		super(parentShell);
	}

	public String getKind() {
		return kind;
	}
	
	@Override
	protected void okPressed() {
		kind = kindCombo.getItem(kindCombo.getSelectionIndex());
		super.okPressed();
	}
	
	@Override
	protected void createFields(Composite container) {
		new Label(container, SWT.NONE).setText("Variable kind");
		kindCombo = new Combo (container, SWT.READ_ONLY);
		kindCombo.setItems (new String [] {KIND_STATE, KIND_INPUT, KIND_OUTPUT});
		kindCombo.select(0);
		kindCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		super.createFields(container);
	}
}
