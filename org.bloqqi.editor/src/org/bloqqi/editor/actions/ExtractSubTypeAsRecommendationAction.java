package org.bloqqi.editor.actions;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.ExtractSubTypeAsRecommendationCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class ExtractSubTypeAsRecommendationAction extends MyWorkbenchPartAction {
	public static final String ID = "org.bloqqi." + ExtractSubTypeAsRecommendationAction.class.getSimpleName();

	public ExtractSubTypeAsRecommendationAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Extract subtype as recommendation");
	}
	
	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	public void run() {
		Shell shell = getEditor().getSite().getShell();
		DiagramType dt = getEditor().getDiagramType();
		
		if (dt.isLocallyEmpty()) {
			MessageDialog.openError(shell, "Subtype is empty",
					"Cannot extract subtype as block since it is empty");
			return;
		}
		
		if (!dt.canExtractSubtypeAsBlock()) {
			MessageDialog.openError(shell, "Cannot extract subtype as block",
					"Cannot extract subtype as block.\n\n"
					+ "The reason is that there is an inherited block that can both "
					+ "be reached from a local node and can reach a local node. "
					+ "This means that the content of this subtype cannot be extracted as one block "
					+ "without introducing data-flow cycles.");
			return;
		}
		
		String diagramTypeName = getInput("Diagram type name", "Enter new diagram type name", "");
		if (diagramTypeName != null) {
			diagramTypeName = diagramTypeName.trim();
			if (!Program.isIdValid(diagramTypeName)) {
				MessageDialog.openError(shell, "Invalid diagram type name",
						"The diagram type name is invalid.");
				return;

			}

			String blockName = getInput("Feature name", "Enter feature name", "");
			if (blockName != null) {
				blockName = blockName.trim();
				if (!Program.isIdValid(blockName)) {
					MessageDialog.openError(shell, "Invalid feature name",
							"The feature name is invalid.");
					return;
				}

				Command cmd = new ExtractSubTypeAsRecommendationCommand(dt, diagramTypeName, blockName);
				execute(cmd);
			}
		}
	}
}
