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

import org.bloqqi.compiler.ast.Block;
import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.Connection;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.Modifiers;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.compiler.ast.StateVariable;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.compiler.ast.TypeUse;
import org.bloqqi.compiler.ast.UserDefType;
import org.bloqqi.compiler.ast.Variable;
import org.bloqqi.editor.tools.BlockCreationTool;
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
				"Create a connection",
				factory, null, null);
		toolEntry.setToolClass(ConnectionCreationToolFeedback.class);
		setToolProperties(toolEntry);
		group.add(toolEntry);
	}

	private void createLiteralCreationToolEntry(PaletteGroup group) {
		CreationFactory factory = createFactory(() -> new Connection(), Connection.class);
		ConnectionCreationToolEntry toolEntry = new ConnectionCreationToolEntry(
				"Literal",
				"Create a literal",
				factory, null, null);
		toolEntry.setToolClass(LiteralCreationTool.class);
		setToolProperties(toolEntry);
		group.add(toolEntry);
	}

	private void createParameterCreationToolEntry(PaletteGroup group) {
		CreationFactory factory = createFactory(() -> new InParameter(), InParameter.class);
		CreationToolEntry toolEntry = new CreationToolEntry(
				"Parameter",
				"Create a parameter",
				factory, null, null);
		toolEntry.setToolClass(ParameterCreationTool.class);
		setToolProperties(toolEntry);
		group.add(toolEntry);
	}
	
	private void createVariableCreationToolEntry(PaletteGroup group) {
		CreationFactory factory = createFactory(() -> new StateVariable(), Variable.class);
		CreationToolEntry toolEntry = new CreationToolEntry(
				"Variable",
				"Create a variable",
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
				if (td != openTypeDecl && showTypeDecl(td)) {
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
			if (showTypeDecl(td)) {
				drawer.add(createToolEntry(td));
			}
		}
		return drawer;
	}

	private boolean showTypeDecl(TypeDecl td) {
		return (td.isDiagramType() && !td.isAbstract())
				|| td.isFunction()
				|| td.isExternalFunction()
				|| td.isStateMachine();
	}

	private ToolEntry createToolEntry(final TypeDecl td) {
		CreationFactory factory = createFactory(
				() -> new Block(new Modifiers(), new TypeUse(td.name()), null),
				Block.class);
		String desc = "Create a block of " + td.name();
		if (td instanceof UserDefType) {
			UserDefType utd = (UserDefType) td;
			if (utd.hasDocString()) {
				String content = utd.getDocString().getDoc().getContent().trim();
				int index = content.indexOf("\n");
				String firstLine = index > 0 ? content.substring(0, index) : content;
				desc += ":\n" + firstLine;
			}
		}
		CreationToolEntry toolEntry = new CreationToolEntry(
				td.name(), desc,
				factory, null, null);
		toolEntry.setToolClass(BlockCreationTool.class);
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
