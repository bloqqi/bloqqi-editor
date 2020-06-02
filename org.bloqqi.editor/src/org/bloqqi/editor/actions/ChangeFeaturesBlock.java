package org.bloqqi.editor.actions;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import java.util.Iterator;

import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.ChangeFeaturesBlockCommand;
import org.bloqqi.editor.editparts.BlockPart;
import org.bloqqi.editor.wizards.features.FeatureWizardChangeBlock;
import org.bloqqi.editor.wizards.features.MyWizardDialog;

public class ChangeFeaturesBlock extends MySelectionAction {
	public static final String ID = "org.bloqqi." + ChangeFeaturesBlock.class.getSimpleName();

	public ChangeFeaturesBlock(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Change features...");
	}

	@Override
	protected boolean calculateEnabled() {
		BlockPart part = getSelected(BlockPart.class);
		if (part != null
				&& !part.getModel().isInherited()
				&& part.getModel().hasAnonymousDiagramType()) {
			DiagramType superDt = getDirectSuperType(part.getModel());
			return superDt != null ? !superDt.featureDecls().isEmpty() : false;
		}
		return false;
	}

	@Override
	public void run() {
		Block block = getSelected(BlockPart.class).getModel().declaredBlock();
		DiagramType superDt = getDirectSuperType(block);

		Shell shell = getEditor().getSite().getShell();
		FeatureWizardChangeBlock wizard = new FeatureWizardChangeBlock(
				superDt,
				block.diagramType(),
				block);
		MyWizardDialog dialog = new MyWizardDialog(shell, wizard);

		if (dialog.open() == Window.OK) {
			ChangeFeaturesBlockCommand cmd =
				new ChangeFeaturesBlockCommand(
						block,
						wizard.getFeatureSelection()
				);
			execute(cmd);
		}
	}

	private DiagramType getDirectSuperType(Block b) {
		Iterator<DiagramType> itr = b.anonymousDiagramType()
				.directSuperTypes().iterator();
		return itr.hasNext() ? itr.next() : null;
	}
}
