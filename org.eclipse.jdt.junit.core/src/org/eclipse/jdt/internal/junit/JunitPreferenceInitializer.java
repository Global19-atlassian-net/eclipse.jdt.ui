/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Davids <sdavids@gmx.de> - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.junit.ui;

import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

/**
 * Default preference value initialization for the
 * <code>org.eclipse.jdt.junit</code> plug-in.
 */
public class JunitPreferenceInitializer extends AbstractPreferenceInitializer {

	/** {@inheritDoc} */
	public void initializeDefaultPreferences() {
		Preferences prefs= JUnitPlugin.getDefault().getPluginPreferences();
		prefs.setDefault(JUnitPreferencesConstants.DO_FILTER_STACK, true);
		prefs.setDefault(JUnitPreferencesConstants.SHOW_ON_ERROR_ONLY, true);

		List defaults= JUnitPreferencesConstants.createDefaultStackFiltersList();
		String[] filters= (String[]) defaults.toArray(new String[defaults.size()]);
		String active= JUnitPreferencesConstants.serializeList(filters);
		prefs.setDefault(JUnitPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, active);
		prefs.setDefault(JUnitPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$
	}
}