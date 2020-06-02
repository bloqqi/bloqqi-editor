package org.bloqqi.editor.tools;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.Activator;
import org.bloqqi.editor.commands.CreateBlockCommand;
import org.bloqqi.editor.preferences.PreferenceConstants;
import org.bloqqi.editor.wizards.features.FeatureWizardBlock;
import org.bloqqi.editor.wizards.specialize.MyWizardDialog;
import org.bloqqi.editor.wizards.specialize.WizardBlock;

public class BlockCreationTool extends AbstractCreationTool {
	@Override
	protected void performCreation(int button) {
		Command cmd = getCurrentCommand();

		boolean performCreation = true;
		if (cmd instanceof CreateBlockCommand) {
			CreateBlockCommand createCmd = (CreateBlockCommand) cmd;

			TypeDecl td = properties.getProgram().lookupType(createCmd.getBlock().getType().name());
			if (td.isDiagramType()) {
				DiagramType diagramType = (DiagramType) td;
				if (!diagramType.featureDecls().isEmpty()) {
					performCreation = showFeatureDialog(createCmd, td, diagramType);
				} else if (diagramType.hasRecommendations()) {
					performCreation = showWizardDialog(createCmd, td, diagramType);
				} else {
					performCreation = possibleShowNameDialog(createCmd);
				}
			} else if (td.isStateMachine()) {
				performCreation = possibleShowNameDialog(createCmd);
			}
		}

		if (performCreation) {
			super.performCreation(button);
		} else {
			setCurrentCommand(null);
		}
	}

	/** Features */
	private boolean showFeatureDialog(CreateBlockCommand createCmd, TypeDecl td, DiagramType diagramType) {
		boolean performCreation;
		Shell shell = properties.getEditor().getSite().getShell();
		String title = "Feature selection";
		String message = "Type \"" + td.name() + "\" has features. Do you want to select features?";
		MessageDialog dialog = new MessageDialog(shell, title, null,
				message, MessageDialog.QUESTION,
				new String[] { "Yes", "No" }, 0);
		if (dialog.open() == 0) {
			performCreation = showFeaturesWizard(createCmd, diagramType);
		} else {
			performCreation = possibleShowNameDialog(createCmd);
		}
		return performCreation;
	}
	private boolean showFeaturesWizard(CreateBlockCommand createCmd, DiagramType dt) {
		Shell shell = properties.getEditor().getSite().getShell();
		FeatureWizardBlock wizard = new FeatureWizardBlock(dt, properties.getEditor().getDiagramType());
		MyWizardDialog dialog = new MyWizardDialog(shell, wizard);

		if (dialog.open() == Window.OK) {
			Block newBlock = wizard.getFeatureSelection().newAnonymousBlock(wizard.getNewName());
			createCmd.setBlock(newBlock);
			return true;
		} else {
			return false;
		}
	}

	/** Recommendations */
	private boolean showWizardDialog(CreateBlockCommand createCmd, TypeDecl td, DiagramType diagramType) {
		boolean performCreation;
		Shell shell = properties.getEditor().getSite().getShell();
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
	private boolean showSpecializationWizard(CreateBlockCommand createCmd, DiagramType dt) {
		Shell shell = properties.getEditor().getSite().getShell();
		WizardBlock wizard = new WizardBlock(dt, properties.getEditor().getDiagramType());
		MyWizardDialog dialog = new MyWizardDialog(shell, wizard);

		if (dialog.open() == Window.OK) {
			Block newBlock = wizard.getFeatureConfiguration().newAnonymousBlock(wizard.getNewName());
			createCmd.setBlock(newBlock);
			createCmd.setNewInParameters(wizard.getNewInParameters());
			return true;
		} else {
			return false;
		}
	}

	private boolean possibleShowNameDialog(CreateBlockCommand createCmd) {
		if (!Activator.isPreferenceSet(PreferenceConstants.ASK_BLOCK_NAME)) {
			return true;
		}

		boolean performCreation = true;
		Shell shell = properties.getEditor().getSite().getShell();
		String name = createCmd.computeNewName();
		InputDialog dialog = new InputDialog(shell, "Block name", "Enter block name", name, null);
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
