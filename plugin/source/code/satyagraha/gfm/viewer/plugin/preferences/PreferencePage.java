package code.satyagraha.gfm.viewer.plugin.preferences;

import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_ALWAYS_GENERATE_HTML;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_API_URL;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_CSS_URL_1;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_CSS_URL_2;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_CSS_URL_3;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_JS_URL_1;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_JS_URL_2;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_JS_URL_3;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_PASSWORD;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_TEMPLATE;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_USERNAME;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_USE_ECLIPSE_CONSOLE;
import static code.satyagraha.gfm.viewer.plugin.preferences.PreferenceConstants.P_USE_TEMP_DIR;

import java.io.File;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import code.satyagraha.gfm.viewer.plugin.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public PreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Define GFM preferences");
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common
     * GUI blocks needed to manipulate various types of preferences. Each field
     * editor knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        
        addField(new BooleanFieldEditor(P_USE_TEMP_DIR, "Use temp dir", parent));
        
        addField(new StringFieldEditor(P_API_URL, "API URL:", parent));
        
        addField(new StringFieldEditor(P_USERNAME, "Username:", parent));
        
        StringFieldEditor passwordFieldEditor = new StringFieldEditor(P_PASSWORD, "Password:", parent);
        passwordFieldEditor.getTextControl(parent).setEchoChar('*');
        addField(passwordFieldEditor);
        
        FileFieldEditor templateEditor = new FileFieldEditor(P_TEMPLATE, "Template File:", true, parent);
        templateEditor.setFilterPath(File.listRoots()[0]);
        addField(templateEditor);
        
        addField(new StringFieldEditor(P_CSS_URL_1, "CSS URL 1:", parent));
        addField(new StringFieldEditor(P_CSS_URL_2, "CSS URL 2:", parent));
        addField(new StringFieldEditor(P_CSS_URL_3, "CSS URL 3:", parent));
        
        addField(new StringFieldEditor(P_JS_URL_1, "JS URL 1:", parent));
        addField(new StringFieldEditor(P_JS_URL_2, "JS URL 2:", parent));
        addField(new StringFieldEditor(P_JS_URL_3, "JS URL 3:", parent));
        
        addField(new BooleanFieldEditor(P_USE_ECLIPSE_CONSOLE, "Use Eclipse Console", parent));
        
        addField(new BooleanFieldEditor(P_ALWAYS_GENERATE_HTML, "Always generate HTML", parent));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
    }

}
