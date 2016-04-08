package org.bloqqi.editor.wizards.specialize;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.bloqqi.compiler.ast.ConfComponent;
import org.bloqqi.compiler.ast.ConfComponentGroup;
import org.bloqqi.compiler.ast.ConfReplaceable;
import org.bloqqi.compiler.ast.ConfReplaceableAlternative;

public class SelectItemMenuListener extends MenuAdapter {
	private final Menu menu;
	private final TreeViewer treeViewer;
	
	public SelectItemMenuListener(Menu menu, TreeViewer treeViewer) {
		this.menu = menu;
		this.treeViewer = treeViewer;
	}
	
	public void menuShown(MenuEvent e) {
		MenuItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++) {
			items[i].dispose();
		}
		Object element = treeViewer.getTree().getSelection()[0].getData();
		if (element instanceof ConfComponentGroup) {
			createMenuComponentGroup(menu, (ConfComponentGroup) element);
		} else if (element instanceof ConfComponent) {
			createMenuComponent(menu, (ConfComponent) element);
		} else if (element instanceof ConfReplaceableAlternative) {
			createMenuReplaceable(menu, (ConfReplaceableAlternative) element);
		}
	}

	private void createMenuComponentGroup(final Menu menu, final ConfComponentGroup group) {
		MenuItem newItem = new MenuItem(menu, SWT.NONE);
		newItem.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				group.setSelected(!group.isSelected());
				treeViewer.update(group, null);
				treeViewer.update(group.getRecommendations().toArray(), null);
			}
		});
		String selectText = group.isSelected() ? "Deselect" : "Select"; 
		newItem.setText(selectText + " " + group.getName());
	}

	private void createMenuComponent(final Menu menu, final ConfComponent component) {
		MenuItem newItem = new MenuItem(menu, SWT.NONE);
		newItem.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				ConfComponentGroup group = component.getGroup();
				group.setSelectedComponent(component);
				group.setSelected(true);
				treeViewer.update(group, null);
				treeViewer.update(group.getRecommendations().toArray(), null);
			}
		});
		newItem.setText("Select " + component.getType().name());
	}
	
	private void createMenuReplaceable(final Menu menu, final ConfReplaceableAlternative alt) {
		MenuItem newItem = new MenuItem(menu, SWT.NONE);
		newItem.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				ConfReplaceable repl = alt.getConfReplaceable();
				repl.setSelectedAlternative(alt);
				treeViewer.update(repl, null);
				treeViewer.update(repl.getAlternatives().toArray(), null);
			}
		});
		newItem.setText("Select " + alt.getType().name());
	}
}
