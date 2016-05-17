package org.bloqqi.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.LineAttributes;

public class FigureUtils {
	public static final LineAttributes LINE_ATTRIBUTES_INHERITED;
	
	public static final Color INLINE_FEEDBACK_COLOR = ColorConstants.lightBlue;
	public static final Color INHERITED_COLOR = new Color(null, 220, 220, 220);
	public static final Color INLINE_COLOR = ColorConstants.white;

	static {
		LINE_ATTRIBUTES_INHERITED = new LineAttributes(1);
		LINE_ATTRIBUTES_INHERITED.style = SWT.LINE_CUSTOM;
		LINE_ATTRIBUTES_INHERITED.dash = new float[]{4, 2};
	}
}
