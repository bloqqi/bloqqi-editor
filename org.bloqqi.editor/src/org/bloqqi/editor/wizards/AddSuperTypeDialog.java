package org.bloqqi.editor.wizards;

import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;


public class AddSuperTypeDialog extends Dialog {
	private Collection<String> possibleSuperTypes;
	private List list;
	private String superType;
	
	protected AddSuperTypeDialog(Shell parentShell, Collection<String> possibleSuperTypes) {
		super(parentShell);
		this.possibleSuperTypes = possibleSuperTypes;
	}

	public String getSuperType() {
		return superType;
	}
	
	protected void okPressed() {
		superType = list.getItem(list.getSelectionIndex());
		super.okPressed();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		list = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		for(String superType: possibleSuperTypes) {
			list.add(superType);
		}
		GridData listGridData = new GridData(GridData.FILL_HORIZONTAL);
		listGridData.heightHint = 100;
		list.setLayoutData(listGridData);
		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});
		
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
		newShell.setText("Add supertype");
	}
	
	private void updateButtons() {
		boolean itemSelected = list.getSelectionCount() == 1;
		getButton(IDialogConstants.OK_ID).setEnabled(itemSelected);
	}
}
