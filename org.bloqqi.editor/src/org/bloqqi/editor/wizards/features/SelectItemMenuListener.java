package org.bloqqi.editor.wizards.features;

import java.util.Set;
import java.util.StringJoiner;

import org.bloqqi.compiler.ast.FeatureSelectionOptional;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

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
		TreeItem element = treeViewer.getTree().getSelection()[0];
		if (element.getData() instanceof FeatureSelectionOptional) {
			createMenuOptionalFeature(menu, (FeatureSelectionOptional) element.getData());
		}
	}

	private void createMenuOptionalFeature(final Menu menu, final FeatureSelectionOptional opt) {
		MenuItem newItem = new MenuItem(menu, SWT.NONE);
		newItem.addSelectionListener(new SelectionAdapter() {
			@Override public void widgetSelected(SelectionEvent e) {
				Set<FeatureSelectionOptional> unselected = opt.setSelected(!opt.isSelected());
				treeViewer.update(opt, null);
				for (FeatureSelectionOptional o: unselected) {
					treeViewer.update(o, null);
				}
			}
		});

		String text = opt.isSelected() ? "Deselect" : "Select";
		text += " " + opt.getName();
		if (!opt.isSelected()) {
			Set<FeatureSelectionOptional> unselects = opt.getUnselectedFeaturesIfSelected();
			if (!unselects.isEmpty()) {
				StringJoiner sj = new StringJoiner(", ");
				for (FeatureSelectionOptional o: unselects) {
					sj.add(o.getName());
				}
				text += " (unselects: " + sj.toString() + ")";
			}
		}
		newItem.setText(text);
	}
}
