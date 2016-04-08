package org.bloqqi.editor.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.editor.BloqqiEditor;


abstract public class GenericAbstractGraphicalPart<T> extends AbstractGraphicalEditPart {
	public GenericAbstractGraphicalPart(T t) {
		setModel(t);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getModel() {
		return (T) super.getModel();
	}
	
	private DiagramType diagramType;
	private BloqqiEditor editor;

	public DiagramType getDiagramType() {
		if (diagramType == null) {
			EditPart current = this;
			while (!(current instanceof DiagramTypePart)) {
				current = current.getParent();
			}
			diagramType = ((DiagramTypePart) current).getModel();
		}
		return diagramType;
	}
	
	public void setEditor(BloqqiEditor editor) {
		this.editor = editor;
	}
	public BloqqiEditor getEditor() {
		return editor;
	}
}
