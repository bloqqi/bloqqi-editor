package org.bloqqi.editor.tools;

import org.bloqqi.compiler.ast.Program;
import org.bloqqi.editor.BloqqiEditor;
import org.eclipse.gef.EditPart;

public class ToolProperties {
	public static final String PROPERTY_ROOT_EDITPART = "root-editpart";
	public static final String PROPERTY_EDITOR = "editor";
	public static final String PROPERTY_PROGRAM = "program";
	
	private EditPart rootEditPart;
	private Program program;
	private BloqqiEditor editor;
	
	public boolean applyProperty(Object key, Object value) {
		if (ToolProperties.PROPERTY_ROOT_EDITPART.equals(key)) {
			rootEditPart = (EditPart) value;
			return true;
		} else if (ToolProperties.PROPERTY_EDITOR.equals(key)) {
			editor = (BloqqiEditor) value;
			return true;
		} else if (ToolProperties.PROPERTY_PROGRAM.equals(key)) {
			program = (Program) value;
			return true;
		}
		return false;
	}
	
	public EditPart getRootEditPart() {
		return rootEditPart;
	}
	
	public Program getProgram() {
		return program;
	}
	
	public BloqqiEditor getEditor() {
		return editor;
	}
}
