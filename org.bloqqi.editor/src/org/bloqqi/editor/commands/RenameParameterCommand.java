package org.bloqqi.editor.commands;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.List;
import org.bloqqi.compiler.ast.Parameter;
	
public class RenameParameterCommand extends RenameNodeCommand {
	private final Parameter declaredParameter;
	
	public RenameParameterCommand(Parameter parameter) {
		super(parameter.declaredParameter(),
				parameter.name(),
				parameter.diagramType(),
				parameter.isInherited());
		this.declaredParameter = parameter.declaredParameter();
	}
	
	@Override
	protected String getNewAccessString(DiagramType forDiagramType) {
		List<? extends Parameter> list;
		if (declaredParameter.isInParameter()) {
			list = forDiagramType.getInParameters();
		} else {
			list = forDiagramType.getOutParameterList();
		}
		Parameter inhPar = null;
		for (Parameter ip: list) {
			if (ip.declaredParameter() == declaredParameter) {
				inhPar = ip;
			}
		}
		return inhPar.accessString();
	}
}
