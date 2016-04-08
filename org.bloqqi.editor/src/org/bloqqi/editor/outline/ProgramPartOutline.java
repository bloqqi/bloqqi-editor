package org.bloqqi.editor.outline;

import java.util.ArrayList;

import java.util.List;

import org.bloqqi.compiler.ast.CompilationUnit;
import org.bloqqi.compiler.ast.Program;

public class ProgramPartOutline extends GenericAbstractTreeEditPart<Program>{
	public ProgramPartOutline(Program p) {
		super(p);
	}

	@Override
	public List<CompilationUnit> getModelChildren() {
		ArrayList<CompilationUnit> list = new ArrayList<CompilationUnit>();
		list.add(getModel().getCompilationUnit(0));
		return list;
	}
}