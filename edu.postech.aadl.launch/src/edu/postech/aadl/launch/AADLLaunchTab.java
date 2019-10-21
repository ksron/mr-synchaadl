package edu.postech.aadl.launch;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AADLLaunchTab extends AbstractLaunchConfigurationTab {

	public Text maudeOptionText;
	public Text maudePathText;
	public Text pspcPathText;


	@Override
	public void createControl(Composite parent) {
		Composite root = new Group(parent, SWT.BORDER);

		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(root);

		createPspcComposite(root);
		createMaudeComposite(root);

		setControl(root);

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public boolean canSave() {
		return (!maudePathText.getText().isEmpty()) && (!pspcPathText.getText().isEmpty());
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		maudePathText.setText(AADLLaunchConfigurationAttributes.MAUDE_PATH);
		if (pspcPathText.getText().isEmpty()) {
			pspcPathText.setText(AADLLaunchConfigurationAttributes.PSPC_PATH);
		}
		maudeOptionText.setText(AADLLaunchConfigurationAttributes.MAUDE_OPTION);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(AADLLaunchConfigurationAttributes.MAUDE_PATH, maudePathText.getText());
		configuration.setAttribute(AADLLaunchConfigurationAttributes.PSPC_PATH, pspcPathText.getText());
		configuration.setAttribute(AADLLaunchConfigurationAttributes.MAUDE_OPTION, maudeOptionText.getText());
	}

	@Override
	public String getName() {
		return "AADL Instance Model Analysis";
	}

	private void createPspcComposite(Composite parent) {
		Label pspcLabel = new Label(parent, SWT.NONE);
		pspcLabel.setText("AADL Property Specification");

		Composite pspcComposite = new Group(parent, SWT.BORDER);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(pspcComposite);
		pspcComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label locationPspcLabel = new Label(pspcComposite, SWT.NONE);
		locationPspcLabel.setText("Location:");

		pspcPathText = new Text(pspcComposite, SWT.BORDER);
		pspcPathText.setMessage("Absolute Property Specification Path");
		pspcPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button pspcPathBrowseButton = new Button(pspcComposite, SWT.BORDER);
		pspcPathBrowseButton.setText("Browse");
		pspcPathBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog exeChooser = new FileDialog(Display.getDefault().getActiveShell());
				String pspcPath = exeChooser.open();
				pspcPathText.setText(pspcPath);
				System.out.println("pspcPath Updated!");
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		pspcComposite.requestLayout();
	}

	private void createMaudeComposite(Composite parent) {
		Label maudeConfigLabel = new Label(parent, SWT.NONE);
		maudeConfigLabel.setText("Maude Configuration");

		Composite maudeComposite = new Group(parent, SWT.BORDER);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(maudeComposite);
		maudeComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Label locationMaudeLabel = new Label(maudeComposite, SWT.NONE);
		locationMaudeLabel.setText("Location:");

		maudePathText = new Text(maudeComposite, SWT.BORDER);
		maudePathText.setMessage("Absolute Maude Path");
		maudePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button maudePathBrowseButton = new Button(maudeComposite, SWT.BORDER);
		maudePathBrowseButton.setText("Browse");
		maudePathBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog exeChooser = new FileDialog(Display.getDefault().getActiveShell());
				String pspcPath = exeChooser.open();
				maudePathText.setText(pspcPath);
				System.out.println("maudePath Updated!");
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		Label optionMaudeLabel = new Label(maudeComposite, SWT.NONE);
		optionMaudeLabel.setText("Options:");

		maudeOptionText = new Text(maudeComposite, SWT.BORDER);
		maudeOptionText.setMessage("-no-prelude prelude.maude smt.maude");
		GridData maudeOptionGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
		maudeOptionGD.horizontalSpan = 2;
		maudeOptionText.setLayoutData(maudeOptionGD);

		maudeComposite.requestLayout();
	}

}
