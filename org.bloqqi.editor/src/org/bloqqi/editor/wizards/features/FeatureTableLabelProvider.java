package org.bloqqi.editor.wizards.features;

import org.bloqqi.compiler.ast.FeatureSelectionOptional;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class FeatureTableLabelProvider implements ITableLabelProvider {
	private final Image optionalImage;
	private final Image uncheckedImage;
	private final Image checkedImage;

	public FeatureTableLabelProvider() {
		optionalImage = new Image(null, getClass().getResourceAsStream("/icons/optional.png"));
		uncheckedImage = new Image(null, getClass().getResourceAsStream("/icons/unchecked.png"));
		checkedImage = new Image(null, getClass().getResourceAsStream("/icons/checked.png"));
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof FeatureSelectionOptional) {
				return optionalImage;
			}
		} else if (columnIndex == 1) {
			if (element instanceof FeatureSelectionOptional) {
				FeatureSelectionOptional opt = (FeatureSelectionOptional) element;
				return opt.isSelected() ? checkedImage : uncheckedImage;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof FeatureSelectionOptional) {
				return ((FeatureSelectionOptional) element).getName();
			} else {
				return element.toString();
			}
		}
		return "";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
}
