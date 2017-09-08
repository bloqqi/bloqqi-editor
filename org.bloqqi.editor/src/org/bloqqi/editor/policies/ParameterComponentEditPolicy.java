package org.bloqqi.editor.policies;

import java.util.List;

import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.editor.commands.ChangeSuperTypesAndParametersCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class ParameterComponentEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		DiagramType dt = (DiagramType) getHost().getParent().getModel();
		Parameter par = (Parameter) getHost().getModel();
		
		if (!par.isInherited()) {
			// This command is created when a parameter is selected.
			// It would be possible to optimize the code by delaying the copying
			// of AST structures and only copy when the command is executed.
			ChangeSuperTypesAndParametersCommand cmd = new ChangeSuperTypesAndParametersCommand(dt);
			cmd.setNewSuperTypesAsBefore();
			if (par.isInParameter()) {
				List<InParameter> inPars = dt.getLocalInParameters().treeCopy().asJavaUtilList();
				inPars.remove(par.declaredParameter());
				cmd.setNewInParameters(inPars);
				cmd.setNewOutParametersAsBefore();
			} else {
				List<OutParameter> outPars = dt.getLocalOutParameters().treeCopy().asJavaUtilList();
				outPars.remove(par.declaredParameter());
				cmd.setNewOutParameters(outPars);
				cmd.setNewInParametersAsBefore();
			}
			return cmd;
		} else {
			return null;
		}
	}
}
