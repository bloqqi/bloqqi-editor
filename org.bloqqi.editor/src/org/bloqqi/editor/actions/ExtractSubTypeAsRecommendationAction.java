package org.bloqqi.editor.actions;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.ExtractSubTypeAsRecommendationCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.InputDialog;
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
		String diagramTypeName = getInput("Diagram type name", "Enter new diagram type name", "");
		if (diagramTypeName != null) {
			String componentName = getInput("Feature name", "Enter feature name", "");
			if (componentName != null) {
				DiagramType dt = getEditor().getDiagramType();
				Command cmd = new ExtractSubTypeAsRecommendationCommand(dt, diagramTypeName, componentName);
				execute(cmd);
			}
		}
	}

	private String getInput(String title, String message, String value) {
		Shell shell = getEditor().getSite().getShell();
		InputDialog dialog = new InputDialog(shell, title, message, value, null);
		
		if(dialog.open() == InputDialog.OK) {
			return dialog.getValue();
		} else {
			return null;
		}
	}
}
