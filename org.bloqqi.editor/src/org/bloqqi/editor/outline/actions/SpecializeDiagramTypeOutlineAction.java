package org.bloqqi.editor.outline.actions;


import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.actions.MySelectionAction;
import org.bloqqi.editor.commands.CreateDiagramTypeSpecializationCommand;
import org.bloqqi.editor.outline.DiagramTypePartOutline;
import org.bloqqi.editor.wizards.specialize.MyWizardDialog;
import org.bloqqi.editor.wizards.specialize.WizardDiagramType;

public class SpecializeDiagramTypeOutlineAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + SpecializeDiagramTypeOutlineAction.class.getSimpleName();

	public SpecializeDiagramTypeOutlineAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Specialize diagram...");
	}

	@Override
	protected boolean calculateEnabled() {
		if (getSelectedObjects().size() == 1
				&& getSelectedObjects().get(0) instanceof DiagramTypePartOutline) {
			return !getDiagramType().isAnonymousType();
		} else {
			return false;
		}
	}
	
	@Override
	public void run() {
		Shell shell = getEditor().getSite().getShell();
		DiagramType diagramType = getDiagramType();
		WizardDiagramType wizard = new WizardDiagramType(diagramType);
		MyWizardDialog dialog = new MyWizardDialog(shell, wizard);
		
		if (dialog.open() == Window.OK) {
			CreateDiagramTypeSpecializationCommand cmd;
			cmd = new CreateDiagramTypeSpecializationCommand(
					diagramType.compUnit(),
					wizard.getFeatureConfiguration(),
					wizard.getNewName(),
					wizard.getNewInParameters());
			execute(cmd);
		}
	}
	
	private DiagramType getDiagramType() {
		return ((DiagramTypePartOutline) getSelectedObjects().get(0)).getModel();
	}
}

