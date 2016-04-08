package org.bloqqi.editor.tools;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.bloqqi.compiler.ast.Literal;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.editor.BloqqiEditor;
import org.bloqqi.editor.commands.CreateConnectionCommand;
import org.bloqqi.editor.commands.CreateLiteralCommand;

public class LiteralCreationTool extends ConnectionCreationTool {
	public static final String PROPERTY_ROOT_EDITPART = "root-editpart";
	public static final String PROPERTY_EDITOR = "editor";
	
	// Set as properties
	private EditPart rootEditPart;
	private BloqqiEditor editor;
	
	private PortsFeedback portsFeedback;

	public LiteralCreationTool() {
		setUnloadWhenFinished(true);
	}
	
	@Override
	public void activate() {
		super.activate();
		portsFeedback = new PortsFeedback(rootEditPart);
		portsFeedback.showInPorts();
	}
	
	@Override
	public void deactivate() {
		hideFeedback();
		super.deactivate();
	}
	
	@Override
	protected void applyProperty(Object key, Object value) {
		if (PROPERTY_ROOT_EDITPART.equals(key)) {
			rootEditPart = (EditPart) value;
			return;
		} else if (PROPERTY_EDITOR.equals(key)) {
			editor = (BloqqiEditor) value;
			return;
		}
		super.applyProperty(key, value);
	}

	@Override
	protected boolean handleButtonUp(int button) {
		boolean rv = super.handleButtonUp(button);
		if (isInState(STATE_CONNECTION_STARTED)) {
			Shell shell = editor.getSite().getShell();
			InputDialog dialog = new InputDialog(shell, "Literal value", "Enter literal value", "", null);

			// Needs to be called before dialog.open by some reason
			CreateConnectionCommand cmd = (CreateConnectionCommand) getCommand();
			
			if (dialog.open() == InputDialog.OK) {
				createLiteral(cmd, dialog.getValue().trim());
			}
			handleFinished();
		}
		return rv;
	}

	private void createLiteral(CreateConnectionCommand cmd, String value) {
		Literal lit = Program.parseLiteral(value);
		if (lit != null) {
			CreateLiteralCommand newCmd = new CreateLiteralCommand(lit, cmd.getTarget(), cmd.getConnection());
			if (newCmd.canExecute()) {
				hideFeedback();
				setState(STATE_TERMINAL);
				setCurrentCommand(newCmd);
				executeCurrentCommand();
			}
		} else {
			MessageDialog.openInformation(
				null,
				"Could not parse literal",
				"Could not parse literal: " + value);
		}
	}

	
	@Override
	public void showSourceFeedback() {
	}
	
	@Override
	public void eraseSourceFeedback() {
	}

	private void hideFeedback() {
		if (portsFeedback != null) {
			portsFeedback.hide();
			portsFeedback = null;
		}
	}
}
