package org.bloqqi.editor.wizards.specialize;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.bloqqi.compiler.ast.ConfComponent;
import org.bloqqi.compiler.ast.ConfComponentGroup;
import org.bloqqi.compiler.ast.ConfReplaceable;
import org.bloqqi.compiler.ast.ConfReplaceableAlternative;

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
			if (element instanceof ConfComponentGroup) {
				return optionalImage;
			} else if (element instanceof ConfReplaceable) {
				return mandatoryImage;
			} else if (element instanceof ConfComponent 
					|| element instanceof ConfReplaceableAlternative) {
				return alternativeImage;
			}
		} else if (columnIndex == 1) {
			if (element instanceof ConfComponentGroup) {
				ConfComponentGroup group = (ConfComponentGroup) element;
				return group.isSelected() ? checkedImage : uncheckedImage;
			} else if (element instanceof ConfComponent) {
				ConfComponent component = (ConfComponent) element;
				ConfComponentGroup group = component.getGroup();
				return group.isSelected() && group.getSelectedComponent() == component
					? checkedImage
					: uncheckedImage;	
			} else if (element instanceof ConfReplaceableAlternative) {
				ConfReplaceableAlternative alt = (ConfReplaceableAlternative) element;
				ConfReplaceable repl = alt.getConfReplaceable();
				return repl.getSelectedAlternative() == alt ? checkedImage : uncheckedImage;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof ConfComponentGroup) {
				return ((ConfComponentGroup) element).getName();
			} else if (element instanceof ConfReplaceable) {
				return ((ConfReplaceable) element).getName();
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
