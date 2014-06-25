package code.satyagraha.gfm.support.api;

import java.net.URI;

public interface WebProxyConfig {
    
    public interface WebProxyData {
        String getProxyUri();
        String getUserId();
        String getPassword();
    }
    
    WebProxyData getWebProxyData(URI uri);

}
