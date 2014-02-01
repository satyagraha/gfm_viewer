package code.satyagraha.gfm.support.api;

import java.io.IOException;
import java.util.List;

public interface Config {

    public boolean useTempDir();
    
    public String getApiUrl();

    public String getUsername();
    
    public String getPassword();

    public String getHtmlTemplate() throws IOException;

    public String getCssText() throws IOException;

    public List<String> getCssUris();

    public String getJsText() throws IOException;
    
    public List<String> getJsUris();

    public boolean useEclipseConsole();
    
    public String getMarkdownExtensions();
    
    public interface Changed {
        
    }
    
}
