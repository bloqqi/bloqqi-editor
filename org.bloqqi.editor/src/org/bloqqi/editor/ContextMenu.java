package org.bloqqi.editor;

import org.bloqqi.editor.actions.AutoLayoutAction;
import org.bloqqi.editor.actions.ChangeAllConnectionRoutersAction;
import org.bloqqi.editor.actions.ChangeConnectionRouterAction;
import org.bloqqi.editor.actions.ChangeSpecializationComponent;
import org.bloqqi.editor.actions.DiagramTypePropertiesAction;
import org.bloqqi.editor.actions.ExtractSubTypeAsRecommendationAction;
import org.bloqqi.editor.actions.InlineAction;
import org.bloqqi.editor.actions.OpenComponentTypeAction;
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
	private static final String SUBMENU_ID_INTERCEPT = "SUBMENU_ID_INTERCEPT";
	
	private final ActionRegistry actionRegistry;

	public ContextMenu(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);

		IMenuManager interceptSubmenu = new MenuManager("Intercept", SUBMENU_ID_INTERCEPT);
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, interceptSubmenu);
		addAction(interceptSubmenu, TargetInterceptAction.ID);
		addAction(interceptSubmenu, SourceInterceptAction.ID);
		
		if (Activator.isPreferenceSet(PreferenceConstants.LAYOUT_OPERATIONS)) {
			IMenuManager layoutSubmenu = new MenuManager("Layout", SUBMENU_ID_LAYOUT);
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, layoutSubmenu);
			addAction(layoutSubmenu, AutoLayoutAction.ID);
			addAction(layoutSubmenu, ChangeAllConnectionRoutersAction.ID);
			addAction(layoutSubmenu, ChangeConnectionRouterAction.ID);
		}
		
		addActionToRest(menu, OpenComponentTypeAction.ID);
		addActionToRest(menu, InlineAction.ID);
		addActionToRest(menu, RenameAction.ID);
		addActionToRest(menu, ChangeSpecializationComponent.ID);
		addActionToRest(menu, ActionFactory.DELETE.getId());
		
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, new Separator());

		addActionToRest(menu, ExtractSubTypeAsRecommendationAction.ID);
		addActionToRest(menu, DiagramTypePropertiesAction.ID);
	}
	
	private void addActionToRest(IMenuManager menu, String key) {
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
