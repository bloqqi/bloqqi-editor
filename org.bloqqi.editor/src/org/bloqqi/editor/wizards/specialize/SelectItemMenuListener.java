package org.bloqqi.editor.wizards.specialize;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.bloqqi.compiler.ast.ConfReplaceable;
import org.bloqqi.compiler.ast.ConfReplaceableAlternative;
import org.bloqqi.compiler.ast.OptionalFeature;
import org.bloqqi.compiler.ast.OptionalFeatureAlternative;

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
		if (element instanceof OptionalFeature) {
			createMenuOptionalFeature(menu, (OptionalFeature) element);
		} else if (element instanceof OptionalFeatureAlternative) {
			createMenuOptionalAlternative(menu, (OptionalFeatureAlternative) element);
		} else if (element instanceof ConfReplaceableAlternative) {
			createMenuReplaceable(menu, (ConfReplaceableAlternative) element);
		}
	}

	private void createMenuOptionalFeature(final Menu menu, final OptionalFeature opt) {
		MenuItem newItem = new MenuItem(menu, SWT.NONE);
		newItem.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				opt.setSelected(!opt.isSelected());
				treeViewer.update(opt, null);
				treeViewer.update(opt.getAlternatives().toArray(), null);
			}
		});
		String selectText = opt.isSelected() ? "Deselect" : "Select"; 
		newItem.setText(selectText + " " + opt.getName());
	}

	private void createMenuOptionalAlternative(final Menu menu, final OptionalFeatureAlternative alt) {
		MenuItem newItem = new MenuItem(menu, SWT.NONE);
		newItem.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				OptionalFeature opt = alt.getOptionalFeature();
				opt.setSelectedAlternative(alt);
				opt.setSelected(true);
				treeViewer.update(opt, null);
				treeViewer.update(opt.getAlternatives().toArray(), null);
			}
		});
		newItem.setText("Select " + alt.getType().name());
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
