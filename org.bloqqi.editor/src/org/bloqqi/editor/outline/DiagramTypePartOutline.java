package org.bloqqi.editor.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.bloqqi.compiler.ast.ASTObservable;
import org.bloqqi.compiler.ast.ASTObserver;
import org.bloqqi.compiler.ast.AnonymousDiagramType;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.BloqqiEditor;

public class DiagramTypePartOutline extends GenericAbstractTreeEditPart<DiagramType> implements ASTObserver {
	private static Image IMG_DIAGRAM_TYPE;
	private static Image IMG_ANONYMOUS_TYPE;
	private static Font NORMAL_FONT;
	private static Font BOLD_FONT;

	private final BloqqiEditor editor;

	public DiagramTypePartOutline(DiagramType dt, final BloqqiEditor editor) {
		super(dt);
		this.editor = editor;
	
		addEditPartListener(new EditPartListener.Stub() {
			public void selectedStateChanged(EditPart editpart) {
				editor.showDiagramType(getModel());
			}
		});
		
		// Initialize static variables
		if (IMG_DIAGRAM_TYPE == null) {
			IMG_DIAGRAM_TYPE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW);
			IMG_ANONYMOUS_TYPE = new Image(null, DiagramTypePartOutline.class.getResourceAsStream("/icons/outline-anonymous-type.png"));
			NORMAL_FONT = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
			BOLD_FONT = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
		}
	}
	
	@Override
	public void activate() {
		if (!isActive()) {
			getModel().addObserver(this);
		}
		super.activate();
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			getModel().deleteObserver(this);
		}
		super.deactivate();
	}
	
	@Override
	public void refreshVisuals() {
		DiagramType dt = getModel();
		String name;
		Image img;
		if (dt instanceof AnonymousDiagramType) {
			AnonymousDiagramType adt = (AnonymousDiagramType) dt;
			name = adt.enclosingComponentName();
			img = IMG_ANONYMOUS_TYPE;
		} else {
			name = dt.name();
			img = IMG_DIAGRAM_TYPE;
		}
		if (dt.getNumSuperType() > 0) {
			name += " : " + dt.getSuperTypes().toString();
		}
		
		setFont();
		setWidgetText(name);
		setWidgetImage(img);
	}

	private void setFont() {
		if (checkTreeItem()) {
			if (editor.getDiagramType() == getModel()) {
				((TreeItem) getWidget()).setFont(BOLD_FONT);
			} else {
				((TreeItem) getWidget()).setFont(NORMAL_FONT);
			}
		}
	}
	
	@Override
	public List<TypeDecl> getModelChildren() {
		List<TypeDecl> list = new ArrayList<>();
		for (Component c: getModel().getLocalComponents()) {
			if (c.hasAnonymousDiagramType()) {
				list.add(c.anonymousDiagramType());
			}
		}
		return list;
	}

	@Override
	public void update(ASTObservable o, Object arg) {
		refresh();
	}
}