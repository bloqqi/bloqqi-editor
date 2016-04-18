package org.bloqqi.editor.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import java.util.Iterator;

import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InheritedComponent;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.ChangeComponentSpecializationCommand;
import org.bloqqi.editor.commands.CreateDiagramTypeSpecializationCommand;
import org.bloqqi.editor.commands.RenameComponentCommand;
import org.bloqqi.editor.editparts.ComponentPart;
import org.bloqqi.editor.wizards.specialize.MyWizardDialog;
import org.bloqqi.editor.wizards.specialize.WizardChangeComponent;
import org.bloqqi.editor.wizards.specialize.WizardComponent;
import org.bloqqi.editor.wizards.specialize.WizardDiagramType;

public class ChangeSpecializationComponent extends MySelectionAction {
	public static final String ID = "org.bloqqi." + ChangeSpecializationComponent.class.getSimpleName();

	public ChangeSpecializationComponent(BloqqiEditor editor) {
		super(editor);
		setId(ID);
		setText("Change specialization...");
	}
	
	@Override
	protected boolean calculateEnabled() {
		ComponentPart part = getSelected(ComponentPart.class);
		if (part != null 
				&& !part.getModel().isInherited() 
				&& part.getModel().hasAnonymousDiagramType()) {
			DiagramType superDt = getDirectSuperType(part.getModel());
			return superDt != null ? superDt.hasRecommendations() : false;
		}
		return false;
	}

	@Override 
	public void run() {
		Component component = getSelected(ComponentPart.class).getModel();
		DiagramType superDt = getDirectSuperType(component);
		
		Shell shell = getEditor().getSite().getShell();
		WizardDiagramType wizard = new WizardChangeComponent(
				superDt, 
				component.diagramType(),
				superDt.specialize(component.anonymousDiagramType()));
		MyWizardDialog dialog = new MyWizardDialog(shell, wizard);
		if (dialog.open() == Window.OK) {
			ChangeComponentSpecializationCommand cmd =
//				new ChangeComponentSpecializationCommand(
//						component,
//						wizard.getConfiguration(),
//						wizard.getNewInParameters()
//				);
		}
		/*if (dialog.open() == Window.OK) {
			CreateDiagramTypeSpecializationCommand cmd;
			cmd = new CreateDiagramTypeSpecializationCommand(
					diagramType.compUnit(),
					wizard.getConfiguration(),
					wizard.getNewName(),
					wizard.getNewInParameters());
			execute(cmd);
		}*/
		//ComponentPart editPart = getSelected(ComponentPart.class);
	}
	
	private DiagramType getDirectSuperType(Component c) {
		Iterator<DiagramType> itr = c.anonymousDiagramType()
				.directSuperTypes().iterator();
		return itr.hasNext() ? itr.next() : null;
	}
}
