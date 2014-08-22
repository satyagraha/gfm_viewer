package code.satyagraha.gfm.support.impl.conn;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.X509TrustManager;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;

@Component(Scope.PLUGIN)
public class X509TrustManagerDefault implements X509TrustManager {
    
    private static Logger LOGGER = Logger.getLogger(X509TrustManagerDefault.class.getPackage().getName());

    public X509TrustManagerDefault() {
        LOGGER.info("");
    }
    
    @Override
    public void checkClientTrusted(X509Certificate[] xcs, String authType) throws CertificateException {
        LOGGER.fine("xcs: " + xcs + " authType: " + authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] xcs, String authType) throws CertificateException {
        LOGGER.fine("xcs: " + xcs + " authType: " + authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        LOGGER.fine("");
        return null;
    }
}
