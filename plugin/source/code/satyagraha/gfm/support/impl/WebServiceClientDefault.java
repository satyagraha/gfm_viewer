package code.satyagraha.gfm.support.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.http.conn.ClientConnectionManager;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.support.api.Config;
import code.satyagraha.gfm.support.api.WebProxyConfig;
import code.satyagraha.gfm.support.api.WebProxyConfig.WebProxyData;
import code.satyagraha.gfm.support.api.WebServiceClient;
import code.satyagraha.gfm.support.impl.conn.ConnUtilities;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;

@Component(Scope.PLUGIN)
public class WebServiceClientDefault implements WebServiceClient {

    private static Logger LOGGER = Logger.getLogger(WebServiceClientDefault.class.getPackage().getName());

    private final Config config;
    private final WebProxyConfig webProxyConfig;
    private final ClientConnectionManager connectionManager;

    public static class Markdown {
        public String text;

        public Markdown() {
        }

        public Markdown(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public WebServiceClientDefault(Config config, WebProxyConfig webProxyConfig, ClientConnectionManager connectionManager) {
        this.config = config;
        this.webProxyConfig = webProxyConfig;
        this.connectionManager = connectionManager;
        LOGGER.fine("");
    }

    @Override
    public String transform(String mdText) {
        if (StringUtils.isBlank(config.getApiUrl())) {
            String responseText = String.format("<pre>\n%s\n</pre>", StringEscapeUtils.escapeHtml4(mdText));
            return responseText;
        }

        Client client = getClient(config.getApiUrl());
        LOGGER.fine("client: " + client);

        String username = config.getUsername();
        if (username != null && username.length() > 0) {
            String password = config.getPassword();
            client.removeFilter(null);
            client.addFilter(new HTTPBasicAuthFilter(username, password));
        }
        client.addFilter(new LoggingFilter(LOGGER));

        WebResource webResource = client.resource(config.getApiUrl());
        Markdown markdown = new Markdown(mdText);
        ClientResponse response = webResource.path("markdown").type(MediaType.APPLICATION_JSON).entity(markdown).accept(MediaType.TEXT_HTML)
                .post(ClientResponse.class);
        String responseText = response.getEntity(String.class);
        return responseText;
    }

    private Client getClient(String endpoint) {
        // set up standard properties
        DefaultApacheHttpClient4Config clientConfig = new DefaultApacheHttpClient4Config();
        Map<String, Object> clientProperties = clientConfig.getProperties();
        clientProperties.put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        clientConfig.getClasses().addAll(ConnUtilities.getJerseyProviders());

        // see if proxy needed
        URI uri;
        try {
            uri = new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        WebProxyData webProxyData = webProxyConfig.getWebProxyData(uri);
        if (webProxyData != null) {
            if (webProxyData.getProxyUri() != null) {
                clientProperties.put(DefaultApacheHttpClient4Config.PROPERTY_PROXY_URI, webProxyData.getProxyUri());
                if (webProxyData.getUserId() != null) {
                    clientProperties.put(DefaultApacheHttpClient4Config.PROPERTY_PROXY_USERNAME, webProxyData.getUserId());
                    if (webProxyData.getPassword() != null) {
                        clientProperties.put(DefaultApacheHttpClient4Config.PROPERTY_PROXY_PASSWORD, webProxyData.getPassword());
                    }
                }
            }
        }

        // set up client properties
        clientProperties.put(DefaultApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER, connectionManager);
        LOGGER.fine("clientProperties(): " + clientProperties);

        // build client
        Client client = ApacheHttpClient4.create(clientConfig);
        return client;
    }

}
