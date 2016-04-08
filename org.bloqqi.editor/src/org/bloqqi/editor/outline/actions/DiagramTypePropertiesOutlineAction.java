package org.bloqqi.editor.outline.actions;


import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.actions.MySelectionAction;
import org.bloqqi.editor.commands.ChangeSuperTypesAndParametersCommand;
import org.bloqqi.editor.outline.DiagramTypePartOutline;
import org.bloqqi.editor.wizards.DiagramTypePropertiesWizard;

public class DiagramTypePropertiesOutlineAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + DiagramTypePropertiesOutlineAction.class.getSimpleName();

	public DiagramTypePropertiesOutlineAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Diagram properties...");
	}

	@Override
	protected boolean calculateEnabled() {
		return getSelectedObjects().size() == 1
			&& getSelectedObjects().get(0) instanceof DiagramTypePartOutline;
	}
	
	@Override
	public void run() {
		DiagramTypePartOutline editPart = (DiagramTypePartOutline) getSelectedObjects().get(0);
		DiagramType diagramType = editPart.getModel();
		
		Shell shell = getEditor().getSite().getShell();
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
