package code.satyagraha.gfm.support.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;

import code.satyagraha.gfm.support.api.Config;

public class ConfigDefault implements Config {

    private static final String API_ROOT_URL = "https://api.github.com";
    private static final Charset UTF_8 = Charset.forName(CharEncoding.UTF_8);
    
    @Override
    public String getApiUrl() {
      return API_ROOT_URL;
    }

    @Override
    public String getUsername() {
        return null;
    }
    
    @Override
    public String getPassword() {
        return null;
    }
    
    @Override
    public String getHtmlTemplate() throws IOException {
        String htmlTemplateText = getResourceAsString("MDTemplate.html");
        return htmlTemplateText;
    }

    @Override
    public String getCssText() throws IOException {
        String cssText = getResourceAsString("MDStyle.css");
        return cssText;
    }

    @Override
    public List<String> getCssUris() {
        return Arrays.asList();
    }

    @Override
    public String getJsText() throws IOException {
        String jsText = getResourceAsString("MDScript.js");
        return jsText;
    }
    
    @Override
    public List<String> getJsUris() {
        return Arrays.asList();
    }

    @Override
    public boolean useTempDir() {
        return false;
    }

    @Override
    public boolean useEclipseConsole() {
        return false;
    }
    
    @Override
    public boolean alwaysGenerateHtml() {
        return false;
    }
    
    private String getResourceAsString(String resourcePath) throws IOException {
        InputStream templateStream = ConfigDefault.class.getResourceAsStream(resourcePath);
        if (templateStream == null) {
            throw new FileNotFoundException(resourcePath);
        }
        String htmlTemplateText = IOUtils.toString(templateStream, UTF_8);
        IOUtils.closeQuietly(templateStream);
        return htmlTemplateText;
    }

    public static class ConfigChangedDefault implements Config.Changed {
        
    }
    
}
