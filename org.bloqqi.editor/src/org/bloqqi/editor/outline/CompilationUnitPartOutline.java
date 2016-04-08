package org.bloqqi.editor.outline;

import java.util.ArrayList;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.gef.EditPart;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.BloqqiEditor;

public class CompilationUnitPartOutline extends GenericAbstractTreeEditPart<CompilationUnit>
		implements ASTObserver, Observer {
	private final BloqqiEditor editor;

	public CompilationUnitPartOutline(CompilationUnit cu, BloqqiEditor editor) {
		super(cu);
		this.editor = editor;
	}
	
	@Override
	public void activate() {
		if (!isActive()) {
			getModel().addObserver(this);
			editor.getOpenDiagramTypeObservable().addObserver(this);
		}
		if (getWidget() instanceof TreeItem) {
			TreeItem treeItem = (TreeItem) getWidget();
			treeItem.setExpanded(true);
		}
		setWidgetText("Diagram types");
		setWidgetImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));
		super.activate();
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			getModel().deleteObserver(this);
			editor.getOpenDiagramTypeObservable().deleteObserver(this);
		}
		super.deactivate();
	}
	
	@Override
	public List<TypeDecl> getModelChildren() {
		ArrayList<TypeDecl> list = new ArrayList<TypeDecl>();
		for (TypeDecl td: getModel().typeDecls()) {
			if (td.isDiagramType()) {
				list.add(td);
			}
		}
		return list;
	}

	@Override
	public void refreshVisuals() {
	}

	@Override
	public void update(ASTObservable o, Object arg) {
		update();
	}

	@Override
	public void update(Observable o, Object arg) {
		refreshVisualsRecursive();
	}
	
	private void update() {
		refresh();
		for (Object child: getChildren()) {
			((EditPart) child).refresh();
		}
	}
}