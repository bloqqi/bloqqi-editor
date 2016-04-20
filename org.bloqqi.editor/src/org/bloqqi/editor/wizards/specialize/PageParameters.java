package org.bloqqi.editor.wizards.specialize;

import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bloqqi.compiler.ast.Parameter;
import org.bloqqi.compiler.ast.SpecializeDiagramType;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

public class PageParameters extends AbstractWizardPage {
	private static final String PAGE_NAME = "SPECIALIZATION_NEW_IN_PARAMETERS";
	
	private final SpecializeDiagramType specializeDt;
	private final SortedMap<String, NewInParameter> newInParameters;
	
	// UI
	private Composite container;
	private Button selectAllButton;
	private CheckboxTableViewer viewer;

	
	public PageParameters(SpecializeDiagramType specializeDt) {
		super(PAGE_NAME);
		this.specializeDt = specializeDt;
		this.newInParameters = new TreeMap<>();
		updateNewInParameters();
		setTitle("Select parameters");
		setDescription("Select inner parameters that are exposed as parameters.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		
		createTable();
		createSelectAllButton();
	    
		setControl(container);
		setPageComplete(true);
	}

	private void createSelectAllButton() {
		selectAllButton = new Button(container, SWT.CHECK);
		selectAllButton.setText("Select all parameters");
		selectAllButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        selectAllParameters(selectAllButton.getSelection());
		        viewer.refresh();
		    }
		});
	}

	private void createTable() {
		viewer = CheckboxTableViewer.newCheckList(container,
					SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.BORDER);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setCheckStateProvider(new MyCheckStateProvider());
		viewer.addCheckStateListener(new MyCheckStateListener());
		viewer.setInput(newInParameters.values());
		
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		createColumns();
	    setTableLayout();
	}

	private void setTableLayout() {
		GridData gridData = new GridData();
	    gridData.verticalAlignment = GridData.FILL;
	    gridData.horizontalSpan = 1;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    viewer.getControl().setLayoutData(gridData);
	}

	private void createColumns() {
		TableViewerColumn tableColumn1 = new TableViewerColumn(viewer, SWT.LEFT);
		tableColumn1.getColumn().setWidth(150);
		tableColumn1.getColumn().setText("Inner Parameter");
		tableColumn1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((NewInParameter) element).getPath();
			}
		});
		
		TableViewerColumn tableColumn2 = new TableViewerColumn(viewer, SWT.LEFT);
		tableColumn2.getColumn().setWidth(150);
		tableColumn2.getColumn().setText("New Parameter Name");
		tableColumn2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((NewInParameter) element).getNewName();
			}
		});
		tableColumn2.setEditingSupport(new ParameterNameEditingSupport(viewer));
	}
	
	private void updateNewInParameters() {
		SortedSet<String> set = specializeDt.getNewInParameters();
		newInParameters.keySet().retainAll(set);
		for (String inParName: set) {
			if (!newInParameters.containsKey(inParName)) {
				newInParameters.put(inParName, new NewInParameter(inParName));
			}
		}
	}
	
	private void selectAllParameters(boolean select) {
		for (NewInParameter in: newInParameters.values()) {
			in.setChecked(select);
		}
	}
	
	public Set<NewInParameter> getNewInParameters() {
		return newInParameters.values().stream()
				.filter((p) -> p.isChecked())
				.collect(Collectors.toSet());
	}

	@Override
	public void firePageChanged() {
		updateNewInParameters();
		viewer.setInput(newInParameters.values());
		if (selectAllButton.getSelection()) {
			selectAllParameters(true);
		}
		viewer.refresh();
	}
	
	public void selectParameter(String parPath, Parameter par) {
		NewInParameter in = newInParameters.get(parPath);
		if (in != null) {
			in.setChecked(true);
			in.setNewName(par.name());
		}
	}
	
	public static class NewInParameter implements Comparable<NewInParameter> {
		private final String path;
		private boolean checked;
		private String newName;
		
		public NewInParameter(String path) {
			this.path = path;
			this.checked = false;
			if (path.indexOf('.') > 0) {
				this.newName =
					path.substring(0, path.indexOf('.'))
					+ path.substring(path.lastIndexOf('.')+1);
			} else {
				this.newName = path;
			}
		}
		
		public String getPath() {
			return path;
		}
		
		public boolean isChecked() {
			return checked;
		}
		
		private void setChecked(boolean checked) {
			this.checked = checked;
		}
		
		public String getNewName() {
			return newName;
		}
		
		private void setNewName(String newName) {
			this.newName = newName;
		}
		
		@Override
		public int compareTo(NewInParameter other) {
			return path.compareTo(other.path);
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof NewInParameter) {
				return compareTo((NewInParameter) other) == 0;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + path.hashCode();
			result = 31 * result + Boolean.hashCode(checked);
			result = 31 * result + newName.hashCode();
			return result;
		}
		
		public String toString() {
			return "[name: " + path + ", add: " + checked + ", newName: " + newName + "]";
		}
	}
	
	private static class MyCheckStateProvider implements ICheckStateProvider {
		@Override
		public boolean isChecked(Object element) {
			if (element instanceof NewInParameter) {
				return ((NewInParameter) element).isChecked();
			}
			return false;
		}

		@Override
		public boolean isGrayed(Object element) {
			return false;
		}
	}

	private static class MyCheckStateListener implements ICheckStateListener {
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			if (event.getElement() instanceof NewInParameter) {
				((NewInParameter) event.getElement()).setChecked(event.getChecked());
			}
		}
	}
	
	private static class ParameterNameEditingSupport extends EditingSupport {
		private final CellEditor editor;
		  
		public ParameterNameEditingSupport(TableViewer viewer) {
			super(viewer);
			editor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((NewInParameter) element).getNewName();
		}

		@Override
		protected void setValue(Object element, Object value) {
			((NewInParameter) element).setNewName(String.valueOf(value));
			getViewer().update(element, null);
		}
	}
}
