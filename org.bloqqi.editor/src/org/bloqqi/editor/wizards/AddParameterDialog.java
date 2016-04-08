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


public class AddParameterDialog extends Dialog {
	private Combo parameterKindCombo;
	private Text nameText;
	private Combo typeCombo;
	
	private boolean inParameter;
	private String name;
	private String type;
	
	
	protected AddParameterDialog(Shell parentShell) {
		super(parentShell);
	}

	public boolean isInParameter() {
		return inParameter;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	
	protected void okPressed() {
		String selectedKind = parameterKindCombo.getItem(parameterKindCombo.getSelectionIndex());
		inParameter = selectedKind.equals("Input");
		name = nameText.getText().trim();
		type = typeCombo.getItem(typeCombo.getSelectionIndex());
		super.okPressed();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		new Label(container, SWT.NONE).setText("Parameter kind");
		parameterKindCombo = new Combo (container, SWT.READ_ONLY);
		parameterKindCombo.setItems (new String [] {"Input", "Output"});
		parameterKindCombo.select(0);
		parameterKindCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
		typeCombo.setItems (new String [] {"Int", "Bool"});
		typeCombo.select(0);
		typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return container;
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
		newShell.setText("Add parameter");
	}
	
	private void updateButtons() {
		boolean isValid = Program.isIdValid(nameText.getText().trim());
		getButton(IDialogConstants.OK_ID).setEnabled(isValid);
	}
}
