package org.bloqqi.editor;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.Variable;
import org.bloqqi.editor.tools.ComponentCreationTool;
import org.bloqqi.editor.tools.ConnectionCreationToolFeedback;
import org.bloqqi.editor.tools.LiteralCreationTool;
import org.bloqqi.editor.tools.VariableCreationTool;


public class Palette extends PaletteRoot {
	private final Program program;
	private final TypeDecl openTypeDecl;
	private final BloqqiEditor editor;

	public Palette(BloqqiEditor editor, Program program, TypeDecl openTypeDecl) {
		this.editor = editor;
		this.program = program;
		this.openTypeDecl = openTypeDecl;
		
		add(createToolsGroup());
		add(createLocalTypesDrawer());
		add(createStandardLibraryDrawer());
	}

	private PaletteGroup createToolsGroup() {
		PaletteGroup group = new PaletteGroup("Tools");
		createSelectionToolEntry(group);
		createConnectionCreationToolEntry(group);
		createLiteralCreationToolEntry(group);
		createVariableCreationToolEntry(group);
		return group;
	}

	private void createSelectionToolEntry(PaletteGroup group) {
		SelectionToolEntry selectionTool = new SelectionToolEntry();
		group.add(selectionTool);
		setDefaultEntry(selectionTool);
	}
	
	private void createConnectionCreationToolEntry(PaletteGroup group) {
		CreationFactory connectionFactory = new ConnectionCreationFactory();
		ConnectionCreationToolEntry toolEntry = new ConnectionCreationToolEntry(
				"Connection",
				"Creates a new connection.", 
				connectionFactory, null, null);
		toolEntry.setToolClass(ConnectionCreationToolFeedback.class);
		toolEntry.setToolProperty(
				ConnectionCreationToolFeedback.PROPERTY_ROOT_EDITPART,
				editor.getRootEditPart());
		group.add(toolEntry);
	}

	private void createLiteralCreationToolEntry(PaletteGroup group) {
		CreationFactory connectionFactory = new ConnectionCreationFactory();
		ConnectionCreationToolEntry toolEntry = new ConnectionCreationToolEntry(
				"Literal",
				"Creates a new Literal.",
				connectionFactory, null, null);
		toolEntry.setToolClass(LiteralCreationTool.class);
		toolEntry.setToolProperty(LiteralCreationTool.PROPERTY_ROOT_EDITPART, editor.getRootEditPart());
		toolEntry.setToolProperty(LiteralCreationTool.PROPERTY_EDITOR, editor);
		group.add(toolEntry);
	}
	
	private void createVariableCreationToolEntry(PaletteGroup group) {
		CreationFactory nodeFactory = new CreationFactory() {
			@Override
			public Object getNewObject() {
				return new Variable();
			}
			@Override
			public Object getObjectType() {
				return Variable.class;
			}
		};
		CreationToolEntry toolEntry;
		toolEntry = new CreationToolEntry("Variable",
						"Creates a new variable",
						nodeFactory, null, null);
		toolEntry.setToolClass(VariableCreationTool.class);
		toolEntry.setToolProperty(VariableCreationTool.PROPERTY_EDITOR, editor);
		group.add(toolEntry);
	}
	
	private PaletteDrawer createLocalTypesDrawer() {
		PaletteDrawer drawer = new PaletteDrawer("Local types");
		drawer.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
		for (CompilationUnit cu: program.getCompilationUnits()) {
			for (TypeDecl td: cu.typeDecls()) {
				if (td != openTypeDecl && (td.isDiagramType() || td.isExternalFunction())) {
					drawer.add(createToolEntry(td));
				}
			}
		}
		return drawer;
	}

	private PaletteDrawer createStandardLibraryDrawer() {
		PaletteDrawer drawer = new PaletteDrawer("Standard lib");
		drawer.setInitialState(PaletteDrawer.INITIAL_STATE_CLOSED);
		for (TypeDecl td: program.getStandardLibrary().typeDecls()) {
			if (td.isDiagramType() || td.isFunction() || td.isExternalFunction()) {
				drawer.add(createToolEntry(td));
			}
		}
		return drawer;
	}

	private ToolEntry createToolEntry(final TypeDecl td) {
		CreationFactory nodeFactory = new CreationFactory() {
			@Override
			public Object getNewObject() {
				return new Component(false, new TypeUse(td.name()), null, true);
			}
			@Override
			public Object getObjectType() {
				return Component.class;
			}
		};
		CreationToolEntry toolEntry;
		toolEntry =	new CreationToolEntry(td.name(),
						"Creates a new component of type " + td.name(),
						nodeFactory, null, null);
		toolEntry.setToolClass(ComponentCreationTool.class);
		toolEntry.setToolProperty(ComponentCreationTool.PROPERTY_PROGRAM, program);
		toolEntry.setToolProperty(ComponentCreationTool.PROPERTY_EDITOR, editor);
		return toolEntry;
	}
	
	private final class ConnectionCreationFactory implements CreationFactory {
		@Override
		public Object getNewObject() {
			return new Connection();
		}

		@Override
		public Object getObjectType() {
			return Connection.class;
		}
	}
}
