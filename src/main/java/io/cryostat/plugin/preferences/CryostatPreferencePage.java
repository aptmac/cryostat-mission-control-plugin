/*
 * Copyright The Cryostat Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cryostat.plugin.preferences;

import io.cryostat.plugin.CryostatPlugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CryostatPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    private IntegerFieldEditor portField;

    public CryostatPreferencePage() {
        super(GRID);
        setPreferenceStore(CryostatPlugin.getDefault().getPreferenceStore());
        setDescription(Messages.CryostatPluginPreferencePage_DESCRIPTION);
    }

    @Override
    public void init(IWorkbench workbench) {}

    @Override
    protected void createFieldEditors() {
        addField(
                new BooleanFieldEditor(
                        PreferenceConstants.P_PLUGIN_ENABLED,
                        Messages.CryostatPluginPreferencePage_ENABLE,
                        getFieldEditorParent()));
        portField =
                new IntegerFieldEditor(
                        PreferenceConstants.P_PLUGIN_PORT,
                        Messages.CryostatPluginPreferencePage_PORT,
                        getFieldEditorParent());
        addField(portField);
        enableCryostatFields(isCryostatPluginEnabled());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(FieldEditor.VALUE)) {
            FieldEditor editor = (FieldEditor) event.getSource();
            if (PreferenceConstants.P_PLUGIN_ENABLED.equals(editor.getPreferenceName())) {
                enableCryostatFields((boolean) event.getNewValue());
            }
        }
    }

    private boolean isCryostatPluginEnabled() {
        return CryostatPlugin.getDefault()
                .getPreferenceStore()
                .getBoolean(PreferenceConstants.P_PLUGIN_ENABLED);
    }

    private void enableCryostatFields(boolean enable) {
        portField.setEnabled(enable, getFieldEditorParent());
    }
}
