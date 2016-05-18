package org.bloqqi.editor.tools;

import org.eclipse.gef.tools.CreationTool;

public abstract class AbstractCreationTool extends CreationTool {
	// Set as properties
	protected ToolProperties properties = new ToolProperties();
	
	@Override
	protected void applyProperty(Object key, Object value) {
		if (!properties.applyProperty(key, value)) {
			super.applyProperty(key, value);
		}
	}
}
