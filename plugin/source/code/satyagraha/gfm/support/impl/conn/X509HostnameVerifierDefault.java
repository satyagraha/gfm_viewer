package code.satyagraha.gfm.support.impl.conn;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;

@Component(Scope.PLUGIN)
public class X509HostnameVerifierDefault implements X509HostnameVerifier {

    private static Logger LOGGER = Logger.getLogger(X509HostnameVerifierDefault.class.getPackage().getName());

    public X509HostnameVerifierDefault() {
        LOGGER.info("");
    }

    @Override
    public void verify(String host, SSLSocket sslSocket) throws IOException {
        LOGGER.info("host: " + host + " sslSocket: " + sslSocket);
    }

    @Override
    public void verify(String host, X509Certificate cert) throws SSLException {
        LOGGER.info("host: " + host + " cert: " + cert);
    }

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        LOGGER.info("host: " + host + " cns: " + asList(cns) + " subjectAlts: " + asList(subjectAlts));
    }

    @Override
    public boolean verify(String host, SSLSession sslSession) {
        LOGGER.info("host: " + host + " sslSession: " + sslSession);
        return true;
    }

}
