package org.bloqqi.editor.outline.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.actions.MySelectionAction;
import org.bloqqi.editor.commands.DeleteDiagramTypeCommand;
import org.bloqqi.editor.outline.DiagramTypePartOutline;

public class DeleteDiagramTypeAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + DeleteDiagramTypeAction.class.getSimpleName();

	public DeleteDiagramTypeAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Delete diagram...");
	}

	@Override
	protected boolean calculateEnabled() {
		if (getSelectedObjects().size() == 1
				&& getSelectedObjects().get(0) instanceof DiagramTypePartOutline) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void run() {
		DiagramType dt = getDiagramType();
		CompilationUnit compUnit = dt.compUnit();
		
		if (numberOfDiagramTypes(compUnit) == 1) {
			Shell shell = getEditor().getSite().getShell();
			String message =
				"Cannot delete the last diagram type!\n\n" +
				"At least one diagram type is needed.";
			MessageDialog.openError(shell, "Cannot delete diagram type", message);
		} else {
			deleteDiagramTypeDialog(dt, compUnit);
		}
	}

	private void deleteDiagramTypeDialog(DiagramType dt, CompilationUnit compUnit) {
		String message = "Are you sure that you want to delete the diagram type \"" + dt.name() + "\"?";
		if (!dt.uses().isEmpty()) {
			message += "\n\nNote that \"" + dt.name() + "\" is used by other diagrams!";
		}
		
		Shell shell = getEditor().getSite().getShell();
		boolean deleteDiagram = MessageDialog.openConfirm(shell, "Delete diagram type", message);
		if(deleteDiagram) {
			execute(new DeleteDiagramTypeCommand(dt, getEditor().getCoordinates()));
			changeShownDiagramType(dt, compUnit);
		}
	}

	private void changeShownDiagramType(DiagramType dt, CompilationUnit compUnit) {
		if (getEditor().getDiagramType() == dt) {
			for (TypeDecl td: compUnit.typeDecls()) {
				if (td.isDiagramType()) {
					getEditor().showDiagramType((DiagramType) td);
				}
			}
		}
	}
	
	private int numberOfDiagramTypes(CompilationUnit compUnit) {
		int sum = 0;
		for (TypeDecl td: compUnit.typeDecls()) {
			if (td.isDiagramType()) {
				sum++;
			}
		}
		return sum;
	}
	
	private DiagramType getDiagramType() {
		return ((DiagramTypePartOutline) getSelectedObjects().get(0)).getModel();
	}
}
