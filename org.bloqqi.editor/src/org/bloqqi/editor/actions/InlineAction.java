package org.bloqqi.editor.actions;

import org.eclipse.gef.commands.Command;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.InheritedComponent;
import org.bloqqi.compiler.ast.InlinedComponent;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.SetComponentInlineCommand;
import org.bloqqi.editor.editparts.ComponentPart;


public class InlineAction extends MySelectionAction {
	public static final String ID = "org.bloqqi." + InlineAction.class.getSimpleName();
	private static final String INLINE_TEXT = "Inline";
	private static final String COLLAPSE_TEXT = "Collapse";
	
	public InlineAction(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText(INLINE_TEXT);
	}

	@Override
	protected boolean calculateEnabled() {
		ComponentPart part = getSelected(ComponentPart.class);
		if (part != null) {
			Component component = part.getModel();
			if (component.isInlined()) {
				setText(COLLAPSE_TEXT);
			} else {
				setText(INLINE_TEXT);
			}
			if (component.isInlined()) {
				return component.inlinedComponent().declaredInDiagramType() == component.diagramType();
			} else {
				return component.type().isDiagramType() && !component.isInherited();
			}
		}
		return false;
	}

	@Override 
	public void run() { 
		Component comp = getSelected(ComponentPart.class).getModel();
		Command cmd = null;
		if (comp.isInlined()) {
			InlinedComponent inlinedComp = (InlinedComponent) comp;
			cmd = new SetComponentInlineCommand(
					getEditor().getCoordinates(),
					inlinedComp.inlinedComponent(),
					false);
		} else if (!comp.isInherited()) {
			InheritedComponent inhComp = (InheritedComponent) comp;
			cmd = new SetComponentInlineCommand(
					getEditor().getCoordinates(),
					inhComp.getDeclaredComponent(),
					true);
		}
		execute(cmd);
	}
}
