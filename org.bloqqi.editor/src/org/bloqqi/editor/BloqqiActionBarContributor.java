package org.bloqqi.editor;

import org.bloqqi.editor.actions.AutoLayoutAction;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.RetargetAction;

public class BloqqiActionBarContributor extends ActionBarContributor {
	@Override
	protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new DeleteRetargetAction());
		
		RetargetAction autoLayout = new RetargetAction(AutoLayoutAction.ID, "Auto layout");
		autoLayout.setToolTipText("Auto layout");
		addRetargetAction(autoLayout);
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
		// Add Undo, Redo and Delete to tool bar
		//toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		//toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		//toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
	    toolBarManager.add(getAction(AutoLayoutAction.ID));
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
	}

	@Override
	protected void declareGlobalActionKeys() {
		// TODO Auto-generated method stub
	}
}
