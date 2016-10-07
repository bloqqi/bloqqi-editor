package org.bloqqi.editor;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;

import java.util.function.Supplier;

import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.Component;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.Modifiers;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.StateVariable;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.Variable;
import org.bloqqi.editor.tools.ComponentCreationTool;
import org.bloqqi.editor.tools.ConnectionCreationToolFeedback;
import org.bloqqi.editor.tools.LiteralCreationTool;
import org.bloqqi.editor.tools.ParameterCreationTool;
import org.bloqqi.editor.tools.ToolProperties;
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
		createParameterCreationToolEntry(group);
		createVariableCreationToolEntry(group);
		return group;
	}

	private void createSelectionToolEntry(PaletteGroup group) {
		SelectionToolEntry selectionTool = new SelectionToolEntry();
		group.add(selectionTool);
		setDefaultEntry(selectionTool);
	}
	
	private void createConnectionCreationToolEntry(PaletteGroup group) {
		CreationFactory factory = createFactory(() -> new Connection(), Connection.class);
		ConnectionCreationToolEntry toolEntry = new ConnectionCreationToolEntry(
				"Connection",
				"Creates a new connection.", 
				factory, null, null);
		toolEntry.setToolClass(ConnectionCreationToolFeedback.class);
		setToolProperties(toolEntry);
		group.add(toolEntry);
	}

	private void createLiteralCreationToolEntry(PaletteGroup group) {
		CreationFactory factory = createFactory(() -> new Connection(), Connection.class);
		ConnectionCreationToolEntry toolEntry = new ConnectionCreationToolEntry(
				"Literal",
				"Creates a new Literal.",
				factory, null, null);
		toolEntry.setToolClass(LiteralCreationTool.class);
		setToolProperties(toolEntry);
		group.add(toolEntry);
	}

	private void createParameterCreationToolEntry(PaletteGroup group) {
		CreationFactory factory = createFactory(() -> new InParameter(), InParameter.class);
		CreationToolEntry toolEntry = new CreationToolEntry(
				"Parameter",
				"Creates a new parameter",
				factory, null, null);
		toolEntry.setToolClass(ParameterCreationTool.class);
		setToolProperties(toolEntry);
		group.add(toolEntry);
	}
	
	private void createVariableCreationToolEntry(PaletteGroup group) {
		CreationFactory factory = createFactory(() -> new StateVariable(), Variable.class);
		CreationToolEntry toolEntry = new CreationToolEntry(
				"Variable",
				"Creates a new variable",
				factory, null, null);
		toolEntry.setToolClass(VariableCreationTool.class);
		setToolProperties(toolEntry);
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
		CreationFactory factory = createFactory(
				() -> new Component(new Modifiers(), new TypeUse(td.name()), null),
				Component.class);
		CreationToolEntry toolEntry = new CreationToolEntry(
				td.name(), "Creates a new component of type " + td.name(),
				factory, null, null);
		toolEntry.setToolClass(ComponentCreationTool.class);
		setToolProperties(toolEntry);
		return toolEntry;
	}
	
	
	/** 
	 * Helper methods
	 */
	private void setToolProperties(CreationToolEntry toolEntry) {
		toolEntry.setToolProperty(ToolProperties.PROPERTY_ROOT_EDITPART,
				editor.getRootEditPart());
		toolEntry.setToolProperty(ToolProperties.PROPERTY_EDITOR, editor);
		toolEntry.setToolProperty(ToolProperties.PROPERTY_PROGRAM, program);
	}
	
	private CreationFactory createFactory(Supplier<Object> supl, Class<?> clazz) {
		return new CreationFactory() {
			@Override
			public Object getNewObject() {
				return supl.get();
			}
			@Override
			public Object getObjectType() {
				return clazz;
			}
		};
	}
}
