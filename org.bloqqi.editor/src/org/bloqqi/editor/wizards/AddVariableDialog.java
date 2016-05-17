package org.bloqqi.editor.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.bloqqi.compiler.ast.Program;


public class AddVariableDialog extends Dialog {
	protected Text nameText;
	protected Combo typeCombo;
	
	protected String name;
	protected String type;
	
	
	public AddVariableDialog(Shell parentShell) {
		super(parentShell);
	}

	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	
	@Override
	protected void okPressed() {
		name = nameText.getText().trim();
		type = typeCombo.getItem(typeCombo.getSelectionIndex());
		super.okPressed();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		createFields(container);
		
		return container;
	}

	protected void createFields(Composite container) {
		new Label(container, SWT.NONE).setText("Name");
		nameText = new Text(container, SWT.NONE);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		});
		
		new Label(container, SWT.NONE).setText("Type");
		typeCombo = new Combo (container, SWT.READ_ONLY);
		typeCombo.setItems (new String [] {"Int", "Bool", "Real"});
		typeCombo.select(0);
		typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		updateButtons();
		return control;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add variable");
	}
	
	private void updateButtons() {
		boolean isValid = Program.isIdValid(nameText.getText().trim());
		getButton(IDialogConstants.OK_ID).setEnabled(isValid);
	}
}
