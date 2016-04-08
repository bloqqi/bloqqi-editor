package org.bloqqi.editor.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;


import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.ComponentParameter;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.Literal;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.editor.BloqqiEditor;

public class DiagramTypePartFactory implements EditPartFactory {
	private final BloqqiEditor editor;
	public DiagramTypePartFactory(BloqqiEditor editor) {
		this.editor = editor;
	}
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		GenericAbstractGraphicalPart<?> part = null;
		if (model instanceof DiagramType) {
			part = new DiagramTypePart((DiagramType) model);
		} else if (model instanceof Component) { 
			part = new ComponentPart((Component) model);
		} else if (model instanceof InParameter) {
			part = new InParameterPart((InParameter) model);
		} else if (model instanceof OutParameter) {
			part = new OutParameterPart((OutParameter) model);
		} else if (model instanceof ComponentParameter) {
			part = new ComponentParameterPart((ComponentParameter) model);
		} else if (model instanceof Literal) {
			part = new LiteralPart((Literal) model);
		}
		if (part != null) {
			part.setEditor(editor);
			return part;
		}
		
		if (model instanceof Connection) {
			return new ConnectionPart((Connection) model);
		}
		return null;
	}
}
