package org.bloqqi.editor;

import java.util.ArrayList;

import org.bloqqi.editor.actions.AutoLayoutAction;
import org.bloqqi.editor.actions.ChangeAllConnectionRoutersAction;
import org.bloqqi.editor.actions.ChangeConnectionRouterAction;
import org.bloqqi.editor.actions.ChangeNameDisplayedAction;
import org.bloqqi.editor.actions.DiagramTypePropertiesAction;
import org.bloqqi.editor.actions.ExtractSubTypeAsRecommendationAction;
import org.bloqqi.editor.actions.InlineAction;
import org.bloqqi.editor.actions.OpenComponentTypeAction;
import org.bloqqi.editor.actions.RenameComponentAction;
import org.bloqqi.editor.actions.RenameParameterAction;
import org.bloqqi.editor.actions.SourceInterceptAction;
import org.bloqqi.editor.actions.TargetInterceptAction;
import org.bloqqi.editor.outline.actions.DeleteDiagramTypeAction;
import org.bloqqi.editor.outline.actions.DiagramTypePropertiesOutlineAction;
import org.bloqqi.editor.outline.actions.NewDiagramTypeAction;
import org.bloqqi.editor.outline.actions.RenameDiagramTypeAction;
import org.bloqqi.editor.outline.actions.SpecializeDiagramTypeOutlineAction;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.part.WorkbenchPart;

public class Actions {
	private final BloqqiEditor editor;
	private final ActionRegistry actionRegistry;
	private final ArrayList<IAction> selectionActions;

	public Actions(BloqqiEditor editor) {
		this.editor = editor;
		this.actionRegistry = new ActionRegistry();
		this.selectionActions = new ArrayList<IAction>();
	}
	
	public void init() {
		createCommonActions();
		createWorkspaceActions();
		createSelectionActions();
		createOutlineActions();
	}

	private void createCommonActions() {
		addAction(new UndoAction(editor));
		addAction(new RedoAction(editor));
		addAction(new PrintAction(editor));
	}

	/**
	 * Actions that do not need selected parts
	 */
	private void createWorkspaceActions() {
		addAction(new AutoLayoutAction(editor));
		addAction(new ChangeNameDisplayedAction(editor));
		addAction(new ChangeAllConnectionRoutersAction(editor));
		addAction(new DiagramTypePropertiesAction(editor));
		addAction(new ExtractSubTypeAsRecommendationAction(editor));
	}

	private void createSelectionActions() {
		addSelectionAction(new DeleteAction((WorkbenchPart)editor));
		addSelectionAction(new ChangeConnectionRouterAction(editor));
		addSelectionAction(new RenameComponentAction(editor));
		addSelectionAction(new RenameParameterAction(editor));
		addSelectionAction(new InlineAction(editor));
		addSelectionAction(new TargetInterceptAction(editor));
		addSelectionAction(new SourceInterceptAction(editor));
		addSelectionAction(new OpenComponentTypeAction(editor));
	}
	
	private void createOutlineActions() {
		addAction(new NewDiagramTypeAction(editor));
		addSelectionAction(new RenameDiagramTypeAction(editor));
		addSelectionAction(new DeleteDiagramTypeAction(editor));
		addSelectionAction(new DiagramTypePropertiesOutlineAction(editor));
		addSelectionAction(new SpecializeDiagramTypeOutlineAction(editor));
	}

	
	private void addAction(IAction action) {
		getActionRegistry().registerAction(action);
	}
	private void addSelectionAction(IAction action) {
		addAction(action);
		getSelectionActions().add(action);
	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public ArrayList<IAction> getSelectionActions() {
		return selectionActions;
	}
}
