package org.bloqqi.editor.tools;

import org.eclipse.gef.tools.ConnectionCreationTool;

public abstract class AbstractConnectionCreationTool extends ConnectionCreationTool {
	// Set as properties
	protected ToolProperties properties = new ToolProperties();
	
	@Override
	protected void applyProperty(Object key, Object value) {
		if (!properties.applyProperty(key, value)) {
			super.applyProperty(key, value);
		}
	}
}
