package org.bloqqi.editor.actions;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.ChangeSuperTypesAndParametersCommand;
import org.bloqqi.editor.wizards.DiagramTypePropertiesWizard;

public class DiagramTypePropertiesAction extends MyWorkbenchPartAction {
	public static final String ID = "org.bloqqi." + DiagramTypePropertiesAction.class.getSimpleName();

	public DiagramTypePropertiesAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Diagram properties...");
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	public void run() {
		Shell shell = getEditor().getSite().getShell();
		DiagramType diagramType = getEditor().getDiagramType();
		DiagramTypePropertiesWizard wizard = new DiagramTypePropertiesWizard(diagramType);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		
		if (dialog.open() == Window.OK) {
			ChangeSuperTypesAndParametersCommand cmd = new ChangeSuperTypesAndParametersCommand(diagramType);
			cmd.setNewSuperTypes(wizard.getNewSuperTypes());
			cmd.setNewInParameters(wizard.getNewInParameters());
			cmd.setNewOutParameters(wizard.getNewOutParameters());
			execute(cmd);
		}
	}
}
