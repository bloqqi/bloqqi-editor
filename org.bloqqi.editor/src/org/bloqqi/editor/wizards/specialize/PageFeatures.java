package org.bloqqi.editor.wizards.specialize;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.bloqqi.compiler.ast.DiagramType;
import org.bloqqi.compiler.ast.FeatureConfiguration;
import org.bloqqi.compiler.ast.MandatoryFeature;
import org.bloqqi.compiler.ast.OptionalFeature;
import org.bloqqi.compiler.ast.Program;
import org.bloqqi.editor.Utils;

public class PageFeatures extends AbstractWizardPage  {
	private static final int COLUMN_CHECKBOX_WIDTH = 20;
	private static final int COLUMN_FEATURE_WIDTH = 200;
	private static final String PAGE_NAME = "SPECIALIZATION_SELECT_FEATURES";

	
	protected DiagramType diagramType;
	protected FeatureConfiguration conf;

	// UI
	protected Composite container;
	protected Text newNameText;
	protected Label nameLabel;
	private TreeViewer treeViewer;

	public PageFeatures() {
		super(PAGE_NAME);
		//setTitle("Specialize diagram type");
		setTitle("Specialize block");
	}

	public void setFeatureConfiguration(FeatureConfiguration conf) {
		this.conf = conf;
	}
	
	public void setDiagramType(DiagramType diagramType) {
		this.diagramType = diagramType;
	}
	
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		
		createSuperTypeUI();
		createNewNameUI();
		createRecommendedBlocksUI();
	
		setControl(container);
		setPageComplete(false);
	}
	
	protected void createSuperTypeUI() {
		new Label(container, SWT.NONE).setText("Specialize type:");
		Text name = new Text(container, SWT.NONE);
		name.setText(diagramType.name());
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		name.setEnabled(false);
	}
	
	protected void createNewNameUI() {
		nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("New name:");
		newNameText = new Text(container, SWT.NONE);
		newNameText.setText("");
		newNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newNameText.setEnabled(true);
		newNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkNameInput();
			}
		});
	}

	protected void createRecommendedBlocksUI() {
		Label label = new Label(container, SWT.NONE);
		label.setText("Features:");
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		treeViewer = new TreeViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new SpecializationContentProvider());
		treeViewer.setLabelProvider(new SpecializationTableLabelProvider());

		// Columns
		TreeColumn columnFeature = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		columnFeature.setWidth(COLUMN_FEATURE_WIDTH);
		TreeColumn columnCheckbox = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		columnCheckbox.setWidth(COLUMN_CHECKBOX_WIDTH);
		columnCheckbox.setResizable(false);
		
		if (!Utils.isOSWindows()) {
			// Don't reorder the column order on Windows due to a bug.
			// This will make the checkbox appear to the right on Windows
			// and on the left on other operating systems.
			treeViewer.getTree().setColumnOrder(new int[]{1,0});
		}

		// Menu
		Menu menu = new Menu(treeViewer.getTree());
		menu.addMenuListener(new SelectItemMenuListener(menu, treeViewer));
		treeViewer.getTree().setMenu(menu);
		
		// Input
		treeViewer.setInput(conf);
		
		// Expand selected features (when changing a specialization)
		if (conf.isRecursive()) {
			expandSelectedFeatures();
		} else {
			treeViewer.expandAll();
		}

		// Layout
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 160;
		treeViewer.getTree().setLayoutData(layoutData);

		// Adjust column width when resizing window
		treeViewer.getTree().addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = treeViewer.getTree().getClientArea();
				int width = area.width-COLUMN_CHECKBOX_WIDTH;
				width -= treeViewer.getTree().getVerticalBar().getSize().x;
				columnFeature.setWidth(width);
			}
		});
	}
	
	private void expandSelectedFeatures() {
		expandSelectedFeatures(conf);
	}
	private void expandSelectedFeatures(FeatureConfiguration conf) {
		for (OptionalFeature opt: conf.getOptionalFeatures()) {
			if (opt.isSelected()) {
				treeViewer.expandToLevel(opt, 1);
				if (opt.getSelectedAlternative().containsChanges()) {
					expandSelectedFeatures(opt.getSelectedAlternative().specialize());
				}
			}
		}
		for (MandatoryFeature m: conf.getMandatoryFeatures()) {
			if (m.isRedeclared()) {
				treeViewer.expandToLevel(m, 1);
				if (m.getSelectedAlternative().containsChanges()) {
					expandSelectedFeatures(m.getSelectedAlternative().specialize());
				}
			}
		}
	}

	protected void checkNameInput() {
		setPageComplete(isNameValid(newNameText.getText()));
	}

	protected boolean isNameValid(String name) {
		return Program.isIdValid(name)
				&& !diagramType.program().isTypeDeclared(name);
	}
	
	protected String getNewName() {
		return newNameText.getText();
	}
}
