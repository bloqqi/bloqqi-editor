package org.bloqqi.editor.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.InParameter;
import org.bloqqi.compiler.ast.OutParameter;
import org.bloqqi.compiler.ast.TypeDecl;
import org.bloqqi.compiler.ast.TypeUse;


public class DiagramTypePropertiesPage extends WizardPage {
	private static final String PAGE_NAME = "PAGE_DIAGRAM_TYPE_PROPERTIES";
	private final DiagramType diagramType;
	
	/** UI */
	private Composite container;
	private List superTypesList;
	private List inParametersList;
	private List outParametersList;

	private Button parametersAddButton;
	private Button parametersRemoveButton;
	private Button parametersMoveUpButton;
	private Button parametersMoveDownButton;

	private Button superTypeAddButton;
	private Button superTypeRemoveButton;
	private Button superTypeMoveDownButton;
	private Button superTypeMoveUpButton;
	
	protected DiagramTypePropertiesPage(DiagramType diagramType) {
		super(PAGE_NAME);
		this.diagramType = diagramType;
		setTitle("Diagram type properties");
		setDescription("Change properties for a diagram type.");
	}

	public java.util.List<String> getSuperTypes() {
		if (diagramType.isAnonymousType()) {
			return Arrays.asList(diagramType.getSuperType(0).toString());
		} else {
			return Arrays.asList(superTypesList.getItems());
		}
	}
	
	public java.util.List<InParameter> getInParameters() {
		ArrayList<InParameter> list = new ArrayList<InParameter>();
		for (String s: inParametersList.getItems()) {
			String name = s.substring(0, s.indexOf(":"));
			String type = s.substring(s.indexOf(":")+1);
			list.add(new InParameter(new TypeUse(type), name));
		}
		return list;
	}

	public java.util.List<OutParameter> getOutParameters() {
		ArrayList<OutParameter> list = new ArrayList<OutParameter>();
		for (String s: outParametersList.getItems()) {
			String name = s.substring(0, s.indexOf(":"));
			String type = s.substring(s.indexOf(":")+1);
			list.add(new OutParameter(new TypeUse(type), name));
		}
		return list;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);

		createNameUI();
		createParametersUI();
		if (!diagramType.isAnonymousType()) {
			createSuperTypesUI();
//			createConflictsUI();
		}

		setControl(container);
		setPageComplete(true);
	}

	private void createNameUI() {
		new Label(container, SWT.NONE).setText("Name:");
		Text name = new Text(container, SWT.NONE);
		name.setText(diagramType.name());
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		name.setEnabled(false);
		addDummyLabel();
	}

	private void createParametersUI() {
		Label parametersLabel = new Label(container, SWT.NONE);
		parametersLabel.setText("Parameters:");
		parametersLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		Composite innerContainer = new Composite(container, SWT.NONE);
		GridLayout innerLayout = new GridLayout(2, false);
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerLayout.horizontalSpacing = 0;
		innerLayout.verticalSpacing = 0;
		innerContainer.setLayout(innerLayout);
		innerContainer.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		Label inputLabel = new Label(innerContainer, SWT.NONE);
		inputLabel.setText("Input:");
		inputLabel.setFont(JFaceResources.getFontRegistry().getItalic(""));
		inputLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		Label outputLabel = new Label(innerContainer, SWT.NONE);
		outputLabel.setText("Output:");
		outputLabel.setFont(JFaceResources.getFontRegistry().getItalic(""));
		outputLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		inParametersList = new List(innerContainer, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		GridData inListGridData = new GridData(GridData.FILL_HORIZONTAL);
		inListGridData.heightHint = 106;
		inListGridData.widthHint = 150;
		inParametersList.setLayoutData(inListGridData);
		for (InParameter in: diagramType.getLocalInParameters()) {
			inParametersList.add(in.name() + ":" + in.type());
		}
		inParametersList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				outParametersList.deselectAll();
				updateParametersButtons();
			}
		});
		
		outParametersList= new List(innerContainer, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		GridData outListGridData = new GridData(GridData.FILL_HORIZONTAL);
		outListGridData.heightHint = 106;
		outListGridData.widthHint = 150;
		outParametersList.setLayoutData(outListGridData);
		for (OutParameter out: diagramType.getLocalOutParameters()) {
			outParametersList.add(out.name() + ":" + out.type());
		}
		outParametersList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				inParametersList.deselectAll();
				updateParametersButtons();
			}
		});
		
		createParametersButtons();
	}
	
	private void createParametersButtons() {
		Composite innerContainer = new Composite(container, SWT.NONE);
		GridLayout innerLayout = new GridLayout(1, false);
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerLayout.horizontalSpacing = 0;
		innerLayout.verticalSpacing = 0;
		innerContainer.setLayout(innerLayout);
		innerContainer.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		new Label(innerContainer, SWT.NONE);

		parametersAddButton = new Button(innerContainer, SWT.NONE);
		parametersAddButton.setText("Add");
		parametersAddButton.addSelectionListener(new ParameterAddAction());
		parametersRemoveButton = new Button(innerContainer, SWT.NONE);
		parametersRemoveButton.setText("Remove");
		parametersRemoveButton.addSelectionListener(new ParameterRemoveAction());
		parametersMoveUpButton = new Button(innerContainer, SWT.NONE);
		parametersMoveUpButton.setText("Move up");
		parametersMoveUpButton.addSelectionListener(new ParameterMoveUpAction());
		parametersMoveDownButton = new Button(innerContainer, SWT.NONE);
		parametersMoveDownButton.setText("Move down");
		parametersMoveDownButton.addSelectionListener(new ParameterMoveDownAction());
		
		updateParametersButtons();
	}
	
	private void createSuperTypesUI() {
		Label label = new Label(container, SWT.NONE);
		label.setText("Supertypes:");
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		superTypesList = new List (container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		for (TypeUse use: diagramType.getSuperTypes()) {
			superTypesList.add(use.getID());
		}
		GridData listGridData = new GridData(GridData.FILL_HORIZONTAL);
		listGridData.heightHint = 100;
		superTypesList.setLayoutData(listGridData);
		superTypesList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateSuperTypeButtons();
			}
		});
		
		createSuperTypesButtons();
	}

	private void createSuperTypesButtons() {
		Composite innerContainer = new Composite(container, SWT.NONE);
		GridLayout innerLayout = new GridLayout(1, false);
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerLayout.horizontalSpacing = 0;
		innerLayout.verticalSpacing = 0;
		innerContainer.setLayout(innerLayout);
		innerContainer.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		superTypeAddButton = new Button(innerContainer, SWT.NONE);
		superTypeAddButton.setText("Add");
		superTypeAddButton.addSelectionListener(new SuperTypeAddAction());
		superTypeRemoveButton = new Button(innerContainer, SWT.NONE);
		superTypeRemoveButton.setText("Remove");
		superTypeRemoveButton.addSelectionListener(new SuperTypeRemoveAction());
		superTypeMoveUpButton = new Button(innerContainer, SWT.NONE);
		superTypeMoveUpButton.setText("Move up");
		superTypeMoveUpButton.addSelectionListener(new SuperTypeMoveUpAction());
		superTypeMoveDownButton = new Button(innerContainer, SWT.NONE);
		superTypeMoveDownButton.setText("Move down");
		superTypeMoveDownButton.addSelectionListener(new SuperTypeMoveDownAction());
		
		updateSuperTypeButtons();
	}
	
	

	
	private Collection<String> getPossibleSuperTypes() {
		LinkedList<String> possibleSuperTypes = new LinkedList<String>();
		for (TypeDecl td: diagramType.compUnit().typeDecls()) {
			if (td.isDiagramType()) {
				possibleSuperTypes.add(((DiagramType) td).name());
			}
		}
		possibleSuperTypes.remove(diagramType.name());
		for (String superType: superTypesList.getItems()) {
			possibleSuperTypes.remove(superType);
		}
		Collections.sort(possibleSuperTypes);
		return possibleSuperTypes;
	}
	
	private void updateUI() {
		updateParametersButtons();
		updateSuperTypeButtons();
		//updateConflictList();
		//updateConflictingTypesList();
	}
	
	private void updateParametersButtons() {
		int inIndex = inParametersList.getSelectionIndex();
		int outIndex = outParametersList.getSelectionIndex();
		int index;
		int itemCount;
		if (inIndex >= 0) {
			index = inIndex;
			itemCount = inParametersList.getItemCount();
		} else {
			index = outIndex;
			itemCount = outParametersList.getItemCount();
		}
		updateButtonsBasedOnSelection(index, itemCount, 
			parametersRemoveButton, parametersMoveUpButton, parametersMoveDownButton);
	}
	
	private void updateSuperTypeButtons() {
		if (!diagramType.isAnonymousType()) {
			int index = superTypesList.getSelectionIndex();
			int itemCount = superTypesList.getItemCount();
			updateButtonsBasedOnSelection(index, itemCount,
					superTypeRemoveButton, superTypeMoveUpButton, superTypeMoveDownButton);
		}
	}
	
	private void updateButtonsBasedOnSelection(int index, int itemCount, 
			Button removeButton, Button upButton, Button downButton) {
		if (index >= 0) {
			removeButton.setEnabled(true);
			upButton.setEnabled(index > 0);
			downButton.setEnabled(index < itemCount-1);
		} else {
			removeButton.setEnabled(false);
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
	}
	
	private List getCurrentParameterList() {
		List list = null;
		if (inParametersList.getSelectionIndex() >= 0) {
			list = inParametersList;
		} else if (outParametersList.getSelectionIndex() >= 0){
			list = outParametersList;
		}
		return list;
	}
	
	private void addDummyLabel() {
		new Label(container, SWT.NONE);
	}
	
	private abstract class AbstractRemoveAction extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			List list = getList();
			list.remove(list.getSelectionIndex());
			updateUI();
		}
		public abstract List getList();
	}
	private abstract class AbstractMoveUpAction extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			List list = getList();
			int index = list.getSelectionIndex();
			String before = list.getItem(index-1);
			String current = list.getItem(index);
			list.setItem(index-1, current);
			list.setItem(index, before);
			list.setSelection(index-1);
			updateUI();
		}
		public abstract List getList();
	}
	private abstract class AbstractMoveDownAction extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			List list = getList();
			int index = list.getSelectionIndex();
			String current = list.getItem(index);
			String after = list.getItem(index+1);
			list.setItem(index, after);
			list.setItem(index+1, current);
			list.setSelection(index+1);
			updateUI();
		}
		public abstract List getList();
	}
	
	private final class ParameterAddAction extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			AddParameterDialog addDialog = new AddParameterDialog(getShell());
			if (addDialog.open() == Window.OK) {
				boolean inParameter = addDialog.isInParameter();
				String name = addDialog.getName();
				String type = addDialog.getType();
				List list = inParameter ? inParametersList : outParametersList;
				
				boolean alreadyExists = false;
				for (String s: inParametersList.getItems()) {
					if (s.substring(0, s.indexOf(":")).equals(name)) {
						alreadyExists = true;
					}
				}
				for (String s: outParametersList.getItems()) {
					if (s.substring(0, s.indexOf(":")).equals(name)) {
						alreadyExists = true;
					}
				}
				if (diagramType.localBlockLookup(name) != null) {
					alreadyExists = true;
				}
				for (DiagramType superType: diagramType.superTypesLinearized()) {
					if (superType.localLookup(name) != null) {
						alreadyExists = true;
					}
				}
				if (alreadyExists) {
					String message = "Parameter \"" + name + "\" cannot be added: name is already used.";
					MessageDialog.openError(getShell(), "Name already used", message);
				} else {
					list.add(name + ":" + type);
					updateUI();
				}
			}
		}
	}
	private final class ParameterRemoveAction extends AbstractRemoveAction {
		public List getList() {
			return getCurrentParameterList();
		}
	}
	private final class ParameterMoveUpAction extends AbstractMoveUpAction {
		public List getList() {
			return getCurrentParameterList();
		}
	}
	private final class ParameterMoveDownAction extends AbstractMoveDownAction {
		public List getList() {
			return getCurrentParameterList();
		}
	}
	
	private final class SuperTypeAddAction extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			AddSuperTypeDialog addDialog = new AddSuperTypeDialog(getShell(), getPossibleSuperTypes());
			if (addDialog.open() == Window.OK) {
				String superType = addDialog.getSuperType();
				superTypesList.add(superType);
				updateUI();
			}
		}
	}
	private final class SuperTypeRemoveAction extends AbstractRemoveAction {
		public List getList() {
			return superTypesList;
		}
	}
	private final class SuperTypeMoveUpAction extends AbstractMoveUpAction {
		public List getList() {
			return superTypesList;
		}
	}
	private final class SuperTypeMoveDownAction extends AbstractMoveDownAction {
		public List getList() {
			return superTypesList;
		}
	}
	
	
	
	
	
	/**
	 * GUI for showing interception conflicts when having multiple supertypes.
	 * This is not equally important anymore since multiple inheritance is not
	 * used that much.
	
	// UI
	private List portList;
	private List typesList;
	
	// Used for feedback
	private Collection<InterceptionConflict> conflicts;

	private void createConflictsUI() {
		Label conflictsLabel = new Label(container, SWT.NONE);
		conflictsLabel.setText("Conflicts:");
		conflictsLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		Composite innerContainer = new Composite(container, SWT.NONE);
		GridLayout innerLayout = new GridLayout(2, false);
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerLayout.horizontalSpacing = 0;
		innerLayout.verticalSpacing = 0;
		innerContainer.setLayout(innerLayout);
		innerContainer.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		Label portLabel = new Label(innerContainer, SWT.NONE);
		portLabel.setText("Port:");
		portLabel.setFont(JFaceResources.getFontRegistry().getItalic(""));
		portLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		Label typeLabel = new Label(innerContainer, SWT.NONE);
		typeLabel.setText("Conflicting types:");
		typeLabel.setFont(JFaceResources.getFontRegistry().getItalic(""));
		typeLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		portList = new List(innerContainer, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		GridData portListGridData = new GridData(GridData.FILL_HORIZONTAL);
		portListGridData.heightHint = 50;
		portListGridData.widthHint = 150;
		portList.setLayoutData(portListGridData);
		portList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateConflictingTypesList();
			}
		});
		
		typesList = new List(innerContainer, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		GridData typesListGridData = new GridData(GridData.FILL_HORIZONTAL);
		typesListGridData.heightHint = 50;
		typesListGridData.widthHint = 150;
		typesList.setLayoutData(typesListGridData);
		
		addDummyLabel();
		
		updateConflictList();
	}

	private void updateConflictList() {
		portList.removeAll();
		conflicts = getConflicts();
		for (InterceptionConflict conflict: conflicts) {
			portList.add(conflict.getAnchor().access().toString());
		}
	}
	
	private void updateConflictingTypesList() {
		typesList.removeAll();
		
		int i = portList.getSelectionIndex();
		if (i >= 0) {
			String selected = portList.getItem(i);
			InterceptionConflict selectedConflict = null;
			for (InterceptionConflict conflict: conflicts) {
				if (conflict.getAnchor().access().toString().equals(selected)) {
					selectedConflict = conflict;
				}
			}
			if (selectedConflict != null) {
				for (DiagramType dt: selectedConflict.getConflictingDiagramTypes()) {
					typesList.add(dt.name());
				}
			}
		}
	}
	
	private Collection<InterceptionConflict> getConflicts() {
		org.bloqqi.compiler.ast.List<TypeUse> newList = new org.bloqqi.compiler.ast.List<TypeUse>();
		for (String type: getSuperTypes()) {
			newList.add(new TypeUse(type));
		}
		org.bloqqi.compiler.ast.List<TypeUse> oldList = diagramType.getSuperTypeList();
		diagramType.setSuperTypeList(newList);
		diagramType.program().flushAllAttributes();

		Collection<InterceptionConflict> conflicts = diagramType.interceptionConflicts();
		
		diagramType.setSuperTypeList(oldList);
		diagramType.program().flushAllAttributes();
		
		return conflicts;
	}
	*/
}