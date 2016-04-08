package org.bloqqi.editor.wizards.specialize;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.bloqqi.compiler.ast.ConfComponent;
import org.bloqqi.compiler.ast.ConfComponentGroup;
import org.bloqqi.compiler.ast.ConfReplaceable;
import org.bloqqi.compiler.ast.ConfReplaceableAlternative;
import org.bloqqi.compiler.ast.SpecializeDiagramType;

public class SpecializationContentProvider implements ITreeContentProvider {
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SpecializeDiagramType) {
			return getChildrenOfNewSpecialization((SpecializeDiagramType) inputElement);
		}
		return new Object[0];
	}

	private Object[] getChildrenOfNewSpecialization(SpecializeDiagramType specDt) {
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll(specDt.getGroups());
		list.addAll(specDt.getReplaceables());
		return list.toArray();
	}

	@Override
	public Object[] getChildren(Object parent) {
		Object[] children = new Object[0];
		if (parent instanceof ConfComponentGroup) {
			ConfComponentGroup group = (ConfComponentGroup) parent;
			if (group.getRecommendations().size() > 1) {
				children = group.getRecommendations().toArray();
			} else {
				children = getChildren(group.getRecommendations().first());
			}
		} else if (parent instanceof ConfComponent) {
			ConfComponent comp = (ConfComponent) parent;
			children = getChildrenOfNewSpecialization(comp.specializeType());
		} else if (parent instanceof ConfReplaceable) {
			ConfReplaceable repl = (ConfReplaceable) parent;
			if (repl.getAlternatives().size() > 1) {
				children = repl.getAlternatives().toArray();
			} else {
				children = getChildren(repl.getAlternatives().first());
			}
		} else if (parent instanceof ConfReplaceableAlternative) {
			ConfReplaceableAlternative alt = (ConfReplaceableAlternative) parent;
			children = getChildrenOfNewSpecialization(alt.specializeType());
		}
		return children;
	}

	@Override
	public Object getParent(Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ConfComponentGroup) {
			ConfComponentGroup group = (ConfComponentGroup) element;
			if (group.getRecommendations().size() > 1) {
				return true;
			}
			return hasChildren(group.getRecommendations().first());
		} else if (element instanceof ConfComponent) {
			ConfComponent comp = (ConfComponent) element;
			return getChildrenOfNewSpecialization(comp.specializeType()).length > 0;
		} else if (element instanceof ConfReplaceable) {
			return ((ConfReplaceable) element).getAlternatives().size() > 0;
		} else if (element instanceof ConfReplaceableAlternative) {
			ConfReplaceableAlternative alt = (ConfReplaceableAlternative) element;
			return getChildrenOfNewSpecialization(alt.specializeType()).length > 0;
		}
		return false;
	}
}
