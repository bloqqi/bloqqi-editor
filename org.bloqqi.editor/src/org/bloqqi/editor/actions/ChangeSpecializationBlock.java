package org.bloqqi.editor.actions;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import java.util.Iterator;

import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.ChangeBlockSpecializationCommand;
import org.bloqqi.editor.editparts.BlockPart;
import org.bloqqi.editor.wizards.specialize.MyWizardDialog;
import org.bloqqi.editor.wizards.specialize.WizardChangeBlock;
import org.bloqqi.editor.wizards.specialize.WizardDiagramType;

public class ChangeSpecializationBlock extends MySelectionAction {
	public static final String ID = "org.bloqqi." + ChangeSpecializationBlock.class.getSimpleName();

	public ChangeSpecializationBlock(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Change specialization...");
	}
	
	@Override
	protected boolean calculateEnabled() {
		BlockPart part = getSelected(BlockPart.class);
		if (part != null 
				&& !part.getModel().isInherited() 
				&& part.getModel().hasAnonymousDiagramType()) {
			DiagramType superDt = getDirectSuperType(part.getModel());
			return superDt != null ? superDt.hasRecommendations() : false;
		}
		return false;
	}

	@Override 
	public void run() {
		Block block = getSelected(BlockPart.class).getModel().declaredBlock();
		DiagramType superDt = getDirectSuperType(block);
		
		Shell shell = getEditor().getSite().getShell();
		WizardDiagramType wizard = new WizardChangeBlock(
				superDt, 
				block.diagramType(),
				block);
		MyWizardDialog dialog = new MyWizardDialog(shell, wizard);

		if (dialog.open() == Window.OK) {
			ChangeBlockSpecializationCommand cmd =
				new ChangeBlockSpecializationCommand(
						block,
						wizard.getFeatureConfiguration(),
						wizard.getNewInParameters()
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
