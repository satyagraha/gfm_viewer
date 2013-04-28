package code.satyagraha.gfm.support.api;

import java.io.IOException;
import java.util.List;

public interface GfmConfig {

    public abstract String getApiUrl();

    public abstract String getUsername();
    
    public abstract String getPassword();

    public abstract String getHtmlTemplate() throws IOException;

    public abstract String getCssText() throws IOException;

    public abstract List<String> getCssUris();

    public abstract String getJsText() throws IOException;
    
    public abstract List<String> getJsUris();
    
}