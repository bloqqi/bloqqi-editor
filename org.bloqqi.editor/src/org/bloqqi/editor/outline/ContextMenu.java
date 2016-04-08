package org.bloqqi.editor.outline;

import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.outline.actions.DeleteDiagramTypeAction;
import org.bloqqi.editor.outline.actions.DiagramTypePropertiesOutlineAction;
import org.bloqqi.editor.outline.actions.NewDiagramTypeAction;
import org.bloqqi.editor.outline.actions.RenameDiagramTypeAction;
import org.bloqqi.editor.outline.actions.SpecializeDiagramTypeOutlineAction;
import org.eclipse.gef.ContextMenuProvider;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;

public class ContextMenu extends ContextMenuProvider {
	private final BloqqiEditor editor;

	public ContextMenu(EditPartViewer viewer, BloqqiEditor editor) {
		super(viewer);
		this.editor = editor;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);
		menu.add(editor.getActionRegistry().getAction(NewDiagramTypeAction.ID));
		menu.add(editor.getActionRegistry().getAction(RenameDiagramTypeAction.ID));
		menu.add(editor.getActionRegistry().getAction(DeleteDiagramTypeAction.ID));
		menu.add(editor.getActionRegistry().getAction(SpecializeDiagramTypeOutlineAction.ID));
		menu.add(editor.getActionRegistry().getAction(DiagramTypePropertiesOutlineAction.ID));
	}
}
