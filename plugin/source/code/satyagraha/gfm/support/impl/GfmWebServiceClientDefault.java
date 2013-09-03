package code.satyagraha.gfm.support.impl;

import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.support.api.GfmWebServiceClient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.impl.provider.entity.StringProvider;

public class GfmWebServiceClientDefault implements GfmWebServiceClient {

    private final GfmConfig gfmConfig;
    private final Logger logger;
    private final ClientConfig clientConfig;

    @XmlRootElement
    public static class Markdown {
        public String text;
        
        public Markdown() {
        }
        
        public Markdown(String text) {
            this.text = text;
        }
    }

    public GfmWebServiceClientDefault(GfmConfig gfmConfig, Logger logger) {
        this.gfmConfig = gfmConfig;
        this.logger = logger;
        logger.info("initializing");
        
        clientConfig = new DefaultClientConfig();
        clientConfig.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        clientConfig.getClasses().add(StringProvider.class);
    }

    @Override
    public String transform(String mdText) {
        Client client = Client.create(clientConfig);
        
        String username = gfmConfig.getUsername();
        if (username != null && username.length() > 0) {
            String password = gfmConfig.getPassword();
            client.removeFilter(null);
            client.addFilter(new HTTPBasicAuthFilter(username, password));
        }
        client.addFilter(new LoggingFilter(logger));
        
        WebResource webResource = client.resource(gfmConfig.getApiUrl());

        Markdown markdown = new Markdown(mdText); 
        ClientResponse response = webResource
                .path("markdown")
                .type(MediaType.APPLICATION_JSON)
                .entity(markdown)
                .accept(MediaType.TEXT_HTML)
                .post(ClientResponse.class);
        String responseText = response.getEntity(String.class);
        return responseText;
    }
}
