
package code.satyagraha.gfm.support.impl.conn;

import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;

@Component(Scope.PLUGIN)
public class SSLSocketFactoryDefault extends SSLSocketFactory {

    private static Logger LOGGER = Logger.getLogger(SSLSocketFactoryDefault.class.getPackage().getName());

    public SSLSocketFactoryDefault(X509TrustManager trustManager, X509HostnameVerifier hostnameVerifier) {
        super(getSSLContext(trustManager), hostnameVerifier);
        LOGGER.info("trustManager: " + trustManager + " hostnameVerifier: " + hostnameVerifier);
    }
    
    private static SSLContext getSSLContext(X509TrustManager trustManager) {
        LOGGER.fine("trustManager: " + trustManager);
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return sslContext;
    }
}
