package org.bloqqi.editor.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.actions.ActionFactory;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.editor.BloqqiEditor;

public class Outline extends ContentOutlinePage {
	private final BloqqiEditor editor; 
	private SashForm sash;

	public Outline(BloqqiEditor editor) {
		super(new TreeViewer());
		this.editor = editor;
	}

	public void createControl(Composite parent) { 
		sash = new SashForm(parent, SWT.VERTICAL); 

		getViewer().createControl(sash); 
		getViewer().setEditDomain(editor.getEditDomain()); 
		getViewer().setEditPartFactory(new EditPartFactory() {
			@Override
			public EditPart createEditPart(EditPart context, Object model) {
				if (model instanceof Program) {
					return new ProgramPartOutline((Program) model);
				} else if (model instanceof CompilationUnit) {
					return new CompilationUnitPartOutline((CompilationUnit) model, editor);
				} else if (model instanceof DiagramType) {
					return new DiagramTypePartOutline((DiagramType) model, editor);
				}
				return null;
			}
		}); 
		getViewer().setContents(editor.getProgram());
		getViewer().setContextMenu(new ContextMenu(getViewer(), editor));
		
		getSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(), 
				editor.getActionRegistry().getAction(ActionFactory.UNDO.getId()));
		getSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(), 
				editor.getActionRegistry().getAction(ActionFactory.REDO.getId()));
	}

	public Control getControl() { 
		return sash; 
	} 
}
