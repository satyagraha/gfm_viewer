package code.satyagraha.gfm.viewer.plugin.preferences;

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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.bushe.swing.event.EventBus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.FrameworkUtil;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.support.api.Config;
import code.satyagraha.gfm.support.impl.ConfigDefault;
import code.satyagraha.gfm.viewer.plugin.Activator;

@Component
public class PreferenceAdapter implements Config {
    
    private static Logger LOGGER = Logger.getLogger(PreferenceAdapter.class.getPackage().getName());

    private static final Charset UTF_8 = Charset.forName(CharEncoding.UTF_8);

    public PreferenceAdapter() {
        String pluginId = FrameworkUtil.getBundle(PreferenceAdapter.class).getSymbolicName();
        IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(pluginId);
        preferences.addPreferenceChangeListener(new IPreferenceChangeListener() {
            
            @Override
            public void preferenceChange(PreferenceChangeEvent event) {
                LOGGER.fine("event: " + event);
                Config.Changed configChanged = new ConfigDefault.ConfigChangedDefault();
                EventBus.publish(configChanged);
            }
        });
    }
    
    @Override
    public boolean useTempDir() {
        return getBooleanPreference(P_USE_TEMP_DIR);
    }
    
    @Override
    public String getApiUrl() {
        return getStringPreference(P_API_URL);
    }

    @Override
    public String getUsername() {
        return getStringPreference(P_USERNAME);
    }
    
    @Override
    public String getPassword() {
        return getStringPreference(P_PASSWORD);
    }
    
    @Override
    public String getHtmlTemplate() throws IOException {
        String templatePath = getStringPreference(P_TEMPLATE);
        return StringUtils.isNotBlank(templatePath)
            ? FileUtils.readFileToString(new File(templatePath), UTF_8)
            : PreferenceInitializer.getConfigDefault().getHtmlTemplate();
    }

    @Override
    public String getCssText() throws IOException {
        List<String> cssUris = getCssUris();
        return cssUris.isEmpty() ? PreferenceInitializer.getConfigDefault().getCssText() : null;
    }

    @Override
    public List<String> getCssUris() {
        List<String> cssUris = new ArrayList<String>();
        String cssUri;
        cssUri = getStringPreference(P_CSS_URL_1);
        if (StringUtils.isNotBlank(cssUri)) cssUris.add(cssUri);
        cssUri = getStringPreference(P_CSS_URL_2);
        if (StringUtils.isNotBlank(cssUri)) cssUris.add(cssUri);
        cssUri = getStringPreference(P_CSS_URL_3);
        if (StringUtils.isNotBlank(cssUri)) cssUris.add(cssUri);
        return cssUris;
    }

    @Override
    public String getJsText() throws IOException {
        List<String> jsUris = getJsUris();
        return jsUris.isEmpty() ? PreferenceInitializer.getConfigDefault().getJsText() : null;
    }
    
    @Override
    public List<String> getJsUris() {
        List<String> jsUris = new ArrayList<String>();
        String jsUri;
        jsUri = getStringPreference(P_JS_URL_1);
        if (StringUtils.isNotBlank(jsUri)) jsUris.add(jsUri);
        jsUri = getStringPreference(P_JS_URL_2);
        if (StringUtils.isNotBlank(jsUri)) jsUris.add(jsUri);
        jsUri = getStringPreference(P_JS_URL_3);
        if (StringUtils.isNotBlank(jsUri)) jsUris.add(jsUri);
        return jsUris;
    }
    
    @Override
    public boolean useEclipseConsole() {
        return getBooleanPreference(P_USE_ECLIPSE_CONSOLE);
    }
    
    private String getStringPreference(String preferenceId) {
        return Activator.getDefault().getPreferenceStore().getString(preferenceId);
    }
    
    private boolean getBooleanPreference(String preferenceId) {
        return Activator.getDefault().getPreferenceStore().getBoolean(preferenceId);
    }
    
}
