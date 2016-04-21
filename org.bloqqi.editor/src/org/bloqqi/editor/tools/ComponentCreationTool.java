package org.bloqqi.editor.tools;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.Activator;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.CreateComponentCommand;
import org.bloqqi.editor.preferences.PreferenceConstants;
import org.bloqqi.editor.wizards.specialize.MyWizardDialog;
import org.bloqqi.editor.wizards.specialize.WizardComponent;

public class ComponentCreationTool extends CreationTool {
	public static final String PROPERTY_PROGRAM = "program";
	public static final String PROPERTY_EDITOR = "editor";
	
	private Program program;
	private BloqqiEditor editor;
	
	@Override
	protected void applyProperty(Object key, Object value) {
		if (PROPERTY_PROGRAM.equals(key)) {
			program = (Program) value;
			return;
		} else if (PROPERTY_EDITOR.equals(key)) {
			editor = (BloqqiEditor) value;
			return;
		}
		super.applyProperty(key, value);
	}
	
	@Override
	protected void performCreation(int button) {
		Command cmd = getCurrentCommand();

		boolean performCreation = true;
		if (cmd instanceof CreateComponentCommand) {
			CreateComponentCommand createCmd = (CreateComponentCommand) cmd;

			TypeDecl td = program.lookupType(createCmd.getComponent().getType().name());
			if (td.isDiagramType()) {
				DiagramType diagramType = (DiagramType) td;
				if (diagramType.hasRecommendations()) {
					performCreation = showWizardDialog(createCmd, td, diagramType);
				} else {
					performCreation = possibleShowNameDialog(createCmd);
				}
			}
		}
		
		if (performCreation) {
			super.performCreation(button);
		} else {
			setCurrentCommand(null);
		}
	}

	private boolean showWizardDialog(CreateComponentCommand createCmd, TypeDecl td, DiagramType diagramType) {
		boolean performCreation;
		Shell shell = editor.getSite().getShell();
		String title = "Specialize type";
		String message = "Type \"" + td.name() + "\" can be specialized. Do you want to specialize it?";
		MessageDialog dialog = new MessageDialog(shell, title, null,
				message, MessageDialog.QUESTION,
				new String[] { "Yes", "No" }, 0);
		if (dialog.open() == 0) {
			performCreation = showSpecializationWizard(createCmd, diagramType);
		} else {
			performCreation = possibleShowNameDialog(createCmd);
		}
		return performCreation;
	}
	
	private boolean showSpecializationWizard(CreateComponentCommand createCmd, DiagramType dt) {
		Shell shell = editor.getSite().getShell();
		WizardComponent wizard = new WizardComponent(dt, editor.getDiagramType());
		MyWizardDialog dialog = new MyWizardDialog(shell, wizard);

		if (dialog.open() == Window.OK) {
			Component newComponent = wizard.getFeatureConfiguration().newAnonymousComponent(wizard.getNewName());
			createCmd.setComponent(newComponent);
			createCmd.setNewInParameters(wizard.getNewInParameters());
			return true;
		} else {
			return false;
		}
	}
	
	private boolean possibleShowNameDialog(CreateComponentCommand createCmd) {
		if (!Activator.isPreferenceSet(PreferenceConstants.ASK_COMPONENT_NAME)) {
			return true;
		}
		
		boolean performCreation = true;
		Shell shell = editor.getSite().getShell();
		String name = createCmd.computeNewName();
		InputDialog dialog = new InputDialog(shell, "Component name", "Enter component name", name, null);
		if(dialog.open() == InputDialog.OK) {
			String newName = dialog.getValue().trim();
			if (name.equals(newName)) {
				createCmd.setSimpleName(name);
			} else {
				createCmd.setName(newName);
			}
		} else {
			performCreation = false;
		}
		return performCreation;
	}
}
