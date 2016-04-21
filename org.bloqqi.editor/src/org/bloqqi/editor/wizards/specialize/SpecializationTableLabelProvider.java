package org.bloqqi.editor.wizards.specialize;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.bloqqi.compiler.ast.MandatoryFeature;
import org.bloqqi.compiler.ast.MandatoryFeatureAlternative;
import org.bloqqi.compiler.ast.OptionalFeature;
import org.bloqqi.compiler.ast.OptionalFeatureAlternative;

public class SpecializationTableLabelProvider implements ITableLabelProvider {
	private final Image mandatoryImage;
	private final Image optionalImage;
	private final Image alternativeImage;
	private final Image uncheckedImage;
	private final Image checkedImage;

	public SpecializationTableLabelProvider() {
		mandatoryImage = new Image(null, getClass().getResourceAsStream("/icons/mandatory.png"));
		optionalImage = new Image(null, getClass().getResourceAsStream("/icons/optional.png"));
		alternativeImage = new Image(null, getClass().getResourceAsStream("/icons/alternative.png"));
		uncheckedImage = new Image(null, getClass().getResourceAsStream("/icons/unchecked.png"));
		checkedImage = new Image(null, getClass().getResourceAsStream("/icons/checked.png"));
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof OptionalFeature) {
				return optionalImage;
			} else if (element instanceof MandatoryFeature) {
				return mandatoryImage;
			} else if (element instanceof OptionalFeatureAlternative 
					|| element instanceof MandatoryFeatureAlternative) {
				return alternativeImage;
			}
		} else if (columnIndex == 1) {
			if (element instanceof OptionalFeature) {
				OptionalFeature opt = (OptionalFeature) element;
				return opt.isSelected() ? checkedImage : uncheckedImage;
			} else if (element instanceof OptionalFeatureAlternative) {
				OptionalFeatureAlternative alt = (OptionalFeatureAlternative) element;
				OptionalFeature opt = alt.getOptionalFeature();
				return opt.isSelected() && opt.getSelectedAlternative() == alt
					? checkedImage
					: uncheckedImage;	
			} else if (element instanceof MandatoryFeatureAlternative) {
				MandatoryFeatureAlternative alt = (MandatoryFeatureAlternative) element;
				MandatoryFeature m = alt.getMandatoryFeature();
				return m.getSelectedAlternative() == alt ? checkedImage : uncheckedImage;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof OptionalFeature) {
				return ((OptionalFeature) element).getName();
			} else if (element instanceof MandatoryFeature) {
				return ((MandatoryFeature) element).getName();
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
