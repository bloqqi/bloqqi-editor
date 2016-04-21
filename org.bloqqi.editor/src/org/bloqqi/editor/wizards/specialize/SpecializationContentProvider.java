package org.bloqqi.editor.wizards.specialize;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.bloqqi.compiler.ast.ConfReplaceable;
import org.bloqqi.compiler.ast.ConfReplaceableAlternative;
import org.bloqqi.compiler.ast.FeatureConfiguration;
import org.bloqqi.compiler.ast.OptionalFeature;
import org.bloqqi.compiler.ast.OptionalFeatureAlternative;

public class SpecializationContentProvider implements ITreeContentProvider {
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof FeatureConfiguration) {
			return getChildrenOfNewSpecialization((FeatureConfiguration) inputElement);
		}
		return new Object[0];
	}

	private Object[] getChildrenOfNewSpecialization(FeatureConfiguration conf) {
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll(conf.getOptionalFeatures());
		list.addAll(conf.getReplaceables());
		return list.toArray();
	}

	@Override
	public Object[] getChildren(Object parent) {
		Object[] children = new Object[0];
		if (parent instanceof OptionalFeature) {
			OptionalFeature opt = (OptionalFeature) parent;
			if (opt.getAlternatives().size() > 1) {
				children = opt.getAlternatives().toArray();
			} else {
				children = getChildren(opt.getAlternatives().first());
			}
		} else if (parent instanceof OptionalFeatureAlternative) {
			OptionalFeatureAlternative alt = (OptionalFeatureAlternative) parent;
			children = getChildrenOfNewSpecialization(alt.specialize());
		} else if (parent instanceof ConfReplaceable) {
			ConfReplaceable repl = (ConfReplaceable) parent;
			if (repl.getAlternatives().size() > 1) {
				children = repl.getAlternatives().toArray();
			} else {
				children = getChildren(repl.getAlternatives().first());
			}
		} else if (parent instanceof ConfReplaceableAlternative) {
			ConfReplaceableAlternative alt = (ConfReplaceableAlternative) parent;
			children = getChildrenOfNewSpecialization(alt.specialize());
		}
		return children;
	}

	@Override
	public Object getParent(Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof OptionalFeature) {
			OptionalFeature opt = (OptionalFeature) element;
			if (opt.getAlternatives().size() > 1) {
				return true;
			}
			return hasChildren(opt.getAlternatives().first());
		} else if (element instanceof OptionalFeatureAlternative) {
			OptionalFeatureAlternative alt = (OptionalFeatureAlternative) element;
			return getChildrenOfNewSpecialization(alt.specialize()).length > 0;
		} else if (element instanceof ConfReplaceable) {
			return ((ConfReplaceable) element).getAlternatives().size() > 0;
		} else if (element instanceof ConfReplaceableAlternative) {
			ConfReplaceableAlternative alt = (ConfReplaceableAlternative) element;
			return getChildrenOfNewSpecialization(alt.specialize()).length > 0;
		}
		return false;
	}
}
