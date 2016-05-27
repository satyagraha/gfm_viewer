package code.satyagraha.gfm.support.impl;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.eclipse.core.net.proxy.IProxyData.HTTPS_PROXY_TYPE;
import static org.eclipse.core.net.proxy.IProxyData.HTTP_PROXY_TYPE;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.support.api.WebProxyConfig;

@Component(Scope.PLUGIN)
public class WebProxyConfigDefault implements WebProxyConfig, AutoCloseable {

    private static Logger LOGGER = Logger.getLogger(WebProxyConfigDefault.class.getPackage().getName());
    
    private BundleContext bundleContext;
    private ServiceReference<IProxyService> proxyServiceReference;
    private IProxyService proxyService;

    private static class WebProxyDataDefault implements WebProxyConfig.WebProxyData {

        private final IProxyData proxyData;

        WebProxyDataDefault(IProxyData proxyData) {
            this.proxyData = proxyData;
        }

        @Override
        public String getProxyUri() {
            String protocol = null;
            if (proxyData.getType().equals(HTTP_PROXY_TYPE)) {
                protocol = "http";
            } else if (proxyData.getType().equals(HTTPS_PROXY_TYPE)) {
                protocol = "http"; // FIXME
            }
            String proxyUri = null;
            if (protocol != null) {
                try {
                    proxyUri = (new URL(protocol, proxyData.getHost(), proxyData.getPort(), "")).toExternalForm();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            return proxyUri;
        }

        @Override
        public String getUserId() {
            return proxyData.getUserId();
        }

        @Override
        public String getPassword() {
            return proxyData.getPassword();
        }
        
        @Override
        public String toString() {
            return reflectionToString(this);
        }

    }

    public WebProxyConfigDefault(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        LOGGER.fine("");
        
        proxyServiceReference = bundleContext.getServiceReference(IProxyService.class);
        if (proxyServiceReference == null) {
            throw new RuntimeException("no service registered for: " + IProxyService.class);
        }
        
        proxyService = bundleContext.getService(proxyServiceReference);
        if (proxyService == null) {
            throw new RuntimeException("no service active for: " + IProxyService.class);
        }
        LOGGER.fine("located proxy service: " + proxyService);
    }

    @Override
    public WebProxyData getWebProxyData(URI uri) {
        WebProxyData webProxyData = null;
        if (proxyService != null) {
            IProxyData[] proxyDataList = proxyService.select(uri);
            LOGGER.fine("for: " + uri + " proxyDataList: " + Arrays.asList(proxyDataList));
            for (IProxyData proxyData : proxyDataList) {
                if (proxyData.getType() != null) {
                    webProxyData = new WebProxyDataDefault(proxyData);
                    break;
                }
            }
        }
        LOGGER.fine("for: " + uri + " proxy: " + webProxyData);
        return webProxyData;
    }

    @Override
    public void close() throws Exception {
        LOGGER.fine("");
        if (bundleContext != null) {
            bundleContext.ungetService(proxyServiceReference);
            proxyService = null;
            proxyServiceReference = null;
            bundleContext = null;
        }
    }

}
