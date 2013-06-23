package code.satyagraha.gfm.viewer.preferences;

import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_USE_TEMP_DIR;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_API_URL;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_CSS_URL_1;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_CSS_URL_2;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_CSS_URL_3;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_JS_URL_1;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_JS_URL_2;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_JS_URL_3;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_PASSWORD;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_TEMPLATE;
import static code.satyagraha.gfm.viewer.preferences.PreferenceConstants.P_USERNAME;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.viewer.plugin.Activator;

public class PreferenceAdapter implements GfmConfig {

    private static final Charset UTF_8 = Charset.forName(CharEncoding.UTF_8);

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
            : PreferenceInitializer.getGfmConfigDefault().getHtmlTemplate();
    }

    @Override
    public String getCssText() throws IOException {
        List<String> cssUris = getCssUris();
        return cssUris.isEmpty() ? PreferenceInitializer.getGfmConfigDefault().getCssText() : null;
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
        return jsUris.isEmpty() ? PreferenceInitializer.getGfmConfigDefault().getJsText() : null;
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
    
    private String getStringPreference(String preferenceId) {
        return Activator.getDefault().getPreferenceStore().getString(preferenceId);
    }
    
    private boolean getBooleanPreference(String preferenceId) {
        return Activator.getDefault().getPreferenceStore().getBoolean(preferenceId);
    }
    
}
