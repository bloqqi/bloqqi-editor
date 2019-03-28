package org.bloqqi.editor;

import org.bloqqi.editor.actions.ShowTypesAction;
import org.bloqqi.editor.actions.ChangeAllConnectionRoutersAction;
import org.bloqqi.editor.actions.ChangeConnectionRouterAction;
import org.bloqqi.editor.actions.ChangeSpecializationBlock;
import org.bloqqi.editor.actions.DiagramTypePropertiesAction;
import org.bloqqi.editor.actions.ExtractSubTypeAsRecommendationAction;
import org.bloqqi.editor.actions.InlineAction;
import org.bloqqi.editor.actions.OpenBlockTypeAction;
import org.bloqqi.editor.actions.RenameAction;
import org.bloqqi.editor.actions.SourceInterceptAction;
import org.bloqqi.editor.actions.TargetInterceptAction;
import org.bloqqi.editor.preferences.PreferenceConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;


public class ContextMenu extends ContextMenuProvider {
	private static final String SUBMENU_ID_LAYOUT = "SUBMENU_ID_LAYOUT";
	
	private final ActionRegistry actionRegistry;

	public ContextMenu(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);

		addActionToEditGroup(menu, TargetInterceptAction.ID);
		addActionToEditGroup(menu, SourceInterceptAction.ID);
		
		if (Activator.isPreferenceSet(PreferenceConstants.LAYOUT_OPERATIONS)) {
			IMenuManager layoutSubmenu = new MenuManager("Layout", SUBMENU_ID_LAYOUT);
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, layoutSubmenu);
			addAction(layoutSubmenu, ShowTypesAction.ID);
			addAction(layoutSubmenu, ChangeAllConnectionRoutersAction.ID);
			addAction(layoutSubmenu, ChangeConnectionRouterAction.ID);
		}
		
		addActionToEditGroup(menu, OpenBlockTypeAction.ID);
		addActionToEditGroup(menu, InlineAction.ID);
		addActionToEditGroup(menu, RenameAction.ID);
		addActionToEditGroup(menu, ChangeSpecializationBlock.ID);
		addActionToEditGroup(menu, ActionFactory.DELETE.getId());
		
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());

		addActionToEditGroup(menu, ExtractSubTypeAsRecommendationAction.ID);
		addActionToEditGroup(menu, DiagramTypePropertiesAction.ID);
	}
	
	private void addActionToEditGroup(IMenuManager menu, String key) {
		IAction action = actionRegistry.getAction(key);
		if (action != null && action.isEnabled()) {
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		}
	}

	private void addAction(IMenuManager menu, String key) {
		IAction action = actionRegistry.getAction(key);
		if (action != null && action.isEnabled()) {
			menu.add(action);
		}
	}
}
