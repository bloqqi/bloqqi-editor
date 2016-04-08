package org.bloqqi.editor;

import java.io.InputStream;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Observable;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.Tool;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.ErrorMessage;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.editor.commands.CreateDiagramTypeCommand;
import org.bloqqi.editor.editparts.DiagramTypePart;
import org.bloqqi.editor.editparts.DiagramTypePartFactory;
import org.bloqqi.editor.outline.Outline;
import org.bloqqi.editor.outline.actions.NewDiagramTypeAction;


public class BloqqiEditor extends EditorPart 
		implements CommandStackListener, ISelectionListener {

	private EditDomain editDomain;
	private GraphicalViewer viewer;
	private Program program;
	private DiagramType diagramType;
	private Observable openDiagramTypeObservable;
	
	private Actions actions;
	
	private boolean isDirty;
	private Coordinates coordinates;
	
	
	public BloqqiEditor() {
		actions = new Actions(this);
		isDirty = false;
		coordinates = new Coordinates();
		openDiagramTypeObservable = new Observable() {
			@Override
			public void notifyObservers(Object obj) {
				// Automatically set changed to true whenever notifyObservers is called
				setChanged();
				super.notifyObservers(obj);
			}
		};
	}
	
	@Override
	public void doSave(final IProgressMonitor monitor) {
		SafeRunner.run(new SafeRunnable() {
			@Override
			public void run() throws Exception {
				IFileEditorInput input = (IFileEditorInput) getEditorInput();
				InputStream is = Utils.programToInputStream(program, coordinates);
				updateErrors();
				input.getFile().setContents(is, false, true, monitor);
				isDirty = false;
				editDomain.getCommandStack().markSaveLocation();
			}
		});
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName().substring(0, input.getName().length()-4));
		initEditDomain();
		actions.init();
		
		program = Utils.readProgram(getEditorInput(), coordinates);
		diagramType = loadOrCreateDiagramType();
		Utils.possibleAutoLayout(diagramType, coordinates);
	}



	private void initEditDomain() {
		editDomain = new DefaultEditDomain(this);
		editDomain.setCommandStack(new PostConditionCommandStack());
		editDomain.getCommandStack().addCommandStackListener(this);
	}

	@Override
	public boolean isDirty() {
		return isDirty || editDomain.getCommandStack().isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) { 
		SashForm form = new SashForm(parent, SWT.HORIZONTAL); 
		createPaletteViewer(form);
		createGraphViewer(form);
		form.setWeights(new int[] { 17, 83 });
		
		updateErrors();
		viewer.setContents(diagramType);
		editDomain.setPaletteRoot(new Palette(this, program, diagramType));
		editDomain.addViewer(viewer);
		
		getSite().setSelectionProvider(viewer);
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
	}

	private DiagramType loadOrCreateDiagramType() throws PartInitException {
		for (TypeDecl td: program.getCompilationUnit(0).typeDecls()) {
			if (td.isDiagramType()) {
				return (DiagramType) td;
			}
		}
		
		DiagramType newDiagramType = null;
		String dialogMessage = 
				"The Bloqqi file \"" + getEditorInput().getName() + "\" is missing a diagram type. " +
				"Please enter a name for a new diagram type.";
		NewDiagramTypeAction action = new NewDiagramTypeAction(this, false, dialogMessage, "Main");
		action.run();
		if (action.getNewDiagramType() != null) {
			newDiagramType = action.getNewDiagramType();
		} else {
			CreateDiagramTypeCommand cmd = new CreateDiagramTypeCommand(program, "Main", false);
			cmd.execute();
			newDiagramType = cmd.getNewDiagramType();
		}
		
		isDirty = true;
		
		return newDiagramType;
	}

	private void createPaletteViewer(Composite parent) {
		PaletteViewer viewer = new PaletteViewer(); 
		viewer.createControl(parent); 
		editDomain.setPaletteViewer(viewer); 
	}

	private void createGraphViewer(Composite parent) {
		viewer = new ScrollingGraphicalViewer(); 
		viewer.createControl(parent);
		viewer.setRootEditPart(new ScalableFreeformRootEditPart() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object getAdapter(Class adapter) {
				if (adapter == ZoomManager.class) {
					return getZoomManager();
				}
				return super.getAdapter(adapter);
			}
		});

		Color lightLightGray = new Color(null, 240, 240, 240);
		//ColorConstants.lightGray
		viewer.getControl().setBackground(lightLightGray);
		viewer.setEditPartFactory(new DiagramTypePartFactory(this));
		viewer.setContextMenu(new ContextMenu(viewer, actions.getActionRegistry()));
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		@SuppressWarnings("rawtypes")
		Iterator iterator = actions.getActionRegistry().getActions();
		while (iterator.hasNext()) {
			Object action = iterator.next();
			if (action instanceof UpdateAction)
				((UpdateAction)action).update();
		}
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) { 
		if (adapter == CommandStack.class) {
			return adapter.cast(editDomain.getCommandStack());
		} else if (adapter == ActionRegistry.class) {
			return adapter.cast(actions.getActionRegistry());
		} else if (adapter == ZoomManager.class) {
			return adapter.cast(viewer.getProperty(ZoomManager.class.toString()));
		} else if (adapter == IContentOutlinePage.class) { 
			return adapter.cast(new Outline(this));
		} 
		return super.getAdapter(adapter); 
	}
	
	@Override
	public void dispose() {
		editDomain.getCommandStack().removeCommandStackListener(this);
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		if (getEditorInput() instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput) getEditorInput();
			try {
				for (IMarker m: input.getFile().findMarkers(IMarker.PROBLEM, true, 1)) {
					m.delete();
				}
			} catch (CoreException ignored ) { }
		}
	}
	
	private void updateErrors() {
		try {
			if (getEditorInput() instanceof IFileEditorInput) {
				IFileEditorInput editor = (IFileEditorInput) getEditorInput();
				for (IMarker m: editor.getFile().findMarkers(IMarker.PROBLEM, true, 1)) {
					m.delete();
				}
				for (ErrorMessage e: program.getCompilationUnit(0).errors()) {
					String message = "";
					if (e.getNode() instanceof DiagramType) {
						message += ((DiagramType) e.getNode()).name() + ": ";
					} else if (e.getNode().diagramType() != null) {
						message += e.getNode().diagramType().name() + ": ";
					}
					message += e.getMessage();
					IMarker m = editor.getFile().createMarker(IMarker.PROBLEM);
					m.setAttribute(IMarker.MESSAGE, message);
					m.setAttribute(IMarker.LOCATION , e.getLine() + ":" + e.getCol());
					m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (equals(getSite().getPage().getActiveEditor())) {
			for (IAction action: actions.getSelectionActions()) {
				if (action instanceof UpdateAction) {
					((UpdateAction) action).update();
				}
			}
		}
	}

	public void refresh() {
		((DiagramTypePart)viewer.getRootEditPart().getContents()).update(null, null);
	}

	public RootEditPart getRootEditPart() {
		return viewer.getRootEditPart();
	}

	public Program getProgram() {
		return program;
	}
	
	public EditDomain getEditDomain() {
		return editDomain;
	}

	public void showDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
		Utils.possibleAutoLayout(diagramType, coordinates);
		viewer.setContents(diagramType);
		editDomain.setPaletteRoot(new Palette(this, program, diagramType));
		openDiagramTypeObservable.notifyObservers();
	}
	
	public Observable getOpenDiagramTypeObservable() {
		return openDiagramTypeObservable;
	}

	public ActionRegistry getActionRegistry() {
		return actions.getActionRegistry();
	}

	public DiagramType getDiagramType() {
		return diagramType;
	}
	
	public void setActiveToolTool(Tool tool) {
		editDomain.setActiveTool(tool);
	}
	
	public Coordinates getCoordinates() {
		return coordinates;
	}
}
