/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
// AW
package org.eclipse.jdt.internal.ui.preferences;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;

import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
	
/*
 * The page for setting general java plugin preferences.
 * See PreferenceConstants to access or change these values through public API.
 */
public class JavaBasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String OPEN_TYPE_HIERARCHY= PreferenceConstants.OPEN_TYPE_HIERARCHY;
	private static final String OPEN_TYPE_HIERARCHY_IN_PERSPECTIVE= PreferenceConstants.OPEN_TYPE_HIERARCHY_IN_PERSPECTIVE;
	private static final String OPEN_TYPE_HIERARCHY_IN_VIEW_PART= PreferenceConstants.OPEN_TYPE_HIERARCHY_IN_VIEW_PART;

	private static final String DOUBLE_CLICK= PreferenceConstants.DOUBLE_CLICK;
	private static final String DOUBLE_CLICK_GOES_INTO= PreferenceConstants.DOUBLE_CLICK_GOES_INTO;
	private static final String DOUBLE_CLICK_EXPANDS= PreferenceConstants.DOUBLE_CLICK_EXPANDS;

	private static final String UPDATE_JAVA_VIEWS= PreferenceConstants.UPDATE_JAVA_VIEWS;
	private static final String UPDATE_ON_SAVE= PreferenceConstants.UPDATE_ON_SAVE;
	private static final String UPDATE_WHILE_EDITING= PreferenceConstants.UPDATE_WHILE_EDITING;

	/**
	 * @deprecated Inline to avoid reference to preference page
	 */	
	public static boolean openTypeHierarchyInPerspective() {
		return PreferenceConstants.OPEN_TYPE_HIERARCHY_IN_PERSPECTIVE.equals(
			PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.OPEN_TYPE_HIERARCHY));
	}

	/**
	 * @deprecated Inline to avoid reference to preference page
	 */	
	public static boolean openTypeHierarchInViewPart() {
		return PreferenceConstants.OPEN_TYPE_HIERARCHY_IN_VIEW_PART.equals(
			PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.OPEN_TYPE_HIERARCHY));
	}

	/**
	 * @deprecated Inline to avoid reference to preference page
	 */	
	public static boolean reusePerspectiveForTypeHierarchy() {
		return false;
		//return PreferenceConstants.getPreferenceStore().getBoolean(OPEN_TYPE_HIERARCHY_REUSE_PERSPECTIVE);
	}


	private ArrayList fCheckBoxes;
	private ArrayList fRadioButtons;
	private ArrayList fTextControls;
	
	private SelectionListener fSelectionListener;
	private ModifyListener fModifyListener;

	public JavaBasePreferencePage() {
		super();
		setPreferenceStore(JavaPlugin.getDefault().getPreferenceStore());
		setDescription(PreferencesMessages.getString("JavaBasePreferencePage.description")); //$NON-NLS-1$
	
		fRadioButtons= new ArrayList();
		fCheckBoxes= new ArrayList();
		fTextControls= new ArrayList();
		
		fSelectionListener= new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
		};
		
		fModifyListener= new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				controlModified(e.widget);
			}
		};
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}		
	
	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		WorkbenchHelp.setHelp(getControl(), IJavaHelpContextIds.JAVA_BASE_PREFERENCE_PAGE);
	}	

	private Button addRadioButton(Composite parent, String label, String key, String value) { 
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		Button button= new Button(parent, SWT.RADIO);
		button.setText(label);
		button.setData(new String[] { key, value });
		button.setLayoutData(gd);

		button.setSelection(value.equals(getPreferenceStore().getString(key)));
		
		fRadioButtons.add(button);
		return button;
	}
	
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= 0;
		layout.verticalSpacing= convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		result.setLayout(layout);
		
		// new Label(composite, SWT.NONE); // spacer
		// Group linkSettings= new Group(result, SWT.NONE);
		// linkSettings.setLayout(new GridLayout());
		// linkSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// linkSettings.setText(PreferencesMessages.getString("JavaBasePreferencePage.linkSettings.text")); //$NON-NLS-1$
		//addCheckBox(linkSettings, PreferencesMessages.getString("JavaBasePreferencePage.linkJavaBrowsingViewsCheckbox.text"), LINK_BROWSING_VIEW_TO_EDITOR); //$NON-NLS-1$
		//addCheckBox(linkSettings, PreferencesMessages.getString("JavaBasePreferencePage.linkPackageView"), LINK_PACKAGES_TO_EDITOR); //$NON-NLS-1$
		//addCheckBox(linkSettings, PreferencesMessages.getString("JavaBasePreferencePage.linkTypeHierarchy"), LINK_TYPEHIERARCHY_TO_EDITOR); //$NON-NLS-1$

		// new Label(result, SWT.NONE); // spacer

		Group updateGroup= new Group(result, SWT.NONE);
		updateGroup.setLayout(new GridLayout());
		updateGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		updateGroup.setText(PreferencesMessages.getString("JavaBasePreferencePage.updateJavaViews")); //$NON-NLS-1$
		addRadioButton(updateGroup, PreferencesMessages.getString("JavaBasePreferencePage.onSave"), UPDATE_JAVA_VIEWS, UPDATE_ON_SAVE); //$NON-NLS-1$
		addRadioButton(updateGroup, PreferencesMessages.getString("JavaBasePreferencePage.whileEditing"), UPDATE_JAVA_VIEWS, UPDATE_WHILE_EDITING);  //$NON-NLS-1$
		Label notice= new Label(updateGroup, SWT.WRAP);
		notice.setText(PreferencesMessages.getString("JavaBasePreferencePage.notice.outliner"));  //$NON-NLS-1$
		GridData noticeData= new GridData(GridData.FILL_HORIZONTAL);
		noticeData.grabExcessHorizontalSpace= true;
		noticeData.widthHint= convertWidthInCharsToPixels(60);
		notice.setLayoutData(noticeData);

		// new Label(result, SWT.NONE); // spacer

		Group doubleClickGroup= new Group(result, SWT.NONE);
		doubleClickGroup.setLayout(new GridLayout());		
		doubleClickGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		doubleClickGroup.setText(PreferencesMessages.getString("JavaBasePreferencePage.doubleclick.action"));  //$NON-NLS-1$
		addRadioButton(doubleClickGroup, PreferencesMessages.getString("JavaBasePreferencePage.doubleclick.gointo"), DOUBLE_CLICK, DOUBLE_CLICK_GOES_INTO); //$NON-NLS-1$
		addRadioButton(doubleClickGroup, PreferencesMessages.getString("JavaBasePreferencePage.doubleclick.expand"), DOUBLE_CLICK, DOUBLE_CLICK_EXPANDS); //$NON-NLS-1$

		// new Label(result, SWT.NONE); // spacer
		
		Group typeHierarchyGroup= new Group(result, SWT.NONE);
		typeHierarchyGroup.setLayout(new GridLayout());		
		typeHierarchyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeHierarchyGroup.setText(PreferencesMessages.getString("JavaBasePreferencePage.openTypeHierarchy")); //$NON-NLS-1$
		addRadioButton(typeHierarchyGroup, PreferencesMessages.getString("JavaBasePreferencePage.inPerspective"), OPEN_TYPE_HIERARCHY, OPEN_TYPE_HIERARCHY_IN_PERSPECTIVE);  //$NON-NLS-1$
		addRadioButton(typeHierarchyGroup, PreferencesMessages.getString("JavaBasePreferencePage.inView"), OPEN_TYPE_HIERARCHY, OPEN_TYPE_HIERARCHY_IN_VIEW_PART); //$NON-NLS-1$

		Dialog.applyDialogFont(result);
		return result;
	}
	
		
	private void controlChanged(Widget widget) {
	}
	
	private void controlModified(Widget widget) {
	}	
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			button.setSelection(store.getDefaultBoolean(key));
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			String[] info= (String[]) button.getData();
			button.setSelection(info[1].equals(store.getDefaultString(info[0])));
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			text.setText(store.getDefaultString(key));
		}
		super.performDefaults();
	}

	/*
	 * @see IPreferencePage#performOk()
	 */
	public boolean performOk() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			store.setValue(key, button.getSelection());
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			if (button.getSelection()) {
				String[] info= (String[]) button.getData();
				store.setValue(info[0], info[1]);
			}
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			store.setValue(key, text.getText());
		}
		
		JavaPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}


}


