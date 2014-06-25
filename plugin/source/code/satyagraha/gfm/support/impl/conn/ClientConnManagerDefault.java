package code.satyagraha.gfm.support.impl.conn;

import java.util.logging.Logger;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.SingleClientConnManager;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;

@Component(Scope.PLUGIN)
public class ClientConnManagerDefault extends SingleClientConnManager {

    private static Logger LOGGER = Logger.getLogger(ClientConnManagerDefault.class.getPackage().getName());

    public ClientConnManagerDefault(SSLSocketFactory sslSocketFactory) {
        super();
        getSchemeRegistry().register(new Scheme("https", 443, sslSocketFactory));
        LOGGER.info("");
    }

}
