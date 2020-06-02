package org.bloqqi.editor.wizards.features;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.bloqqi.compiler.ast.FeatureSelection;

public class FeatureContentProvider implements ITreeContentProvider {
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof FeatureSelection) {
			return getChildrenOfNewSpecialization((FeatureSelection) inputElement);
		}
		return new Object[0];
	}

	private Object[] getChildrenOfNewSpecialization(FeatureSelection selection) {
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll(selection.getOptionalFeatures());
		return list.toArray();
	}

	@Override
	public Object[] getChildren(Object parent) {
		Object[] children = new Object[0];
		return children;
	}

	@Override
	public Object getParent(Object element) {
		return null;  // TODO: When does this happen?
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}
