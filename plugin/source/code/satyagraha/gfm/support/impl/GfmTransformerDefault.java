package code.satyagraha.gfm.support.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.support.api.GfmTransformer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.impl.provider.entity.StringProvider;

public class GfmTransformerDefault implements GfmTransformer {

    @XmlRootElement
    public static class Markdown {
        public String text;
        
        public Markdown() {
        }
        
        public Markdown(String text) {
            this.text = text;
        }
    }
    
    private GfmConfig gfmConfig;
    private Logger logger;
    private ClientConfig clientConfig;
    
    @Override
    public void setConfig(GfmConfig gfmConfig, Logger logger) {
        this.gfmConfig = gfmConfig;
        this.logger = logger;
        logger.info("initializing");
        
        clientConfig = new DefaultClientConfig();
        clientConfig.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        clientConfig.getClasses().add(StringProvider.class);
    }
    
    @Override
    public void transformMarkdownFile(File mdFile, File htFile) throws IOException {
        String mdText = FileUtils.readFileToString(mdFile);
        String htText = transformMarkdownText(mdText);
        CompiledTemplate htmlTemplate = TemplateCompiler.compileTemplate(gfmConfig.getHtmlTemplate());
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("title", htFile.toString());
        vars.put("content", htText);
        vars.put("cssText", gfmConfig.getCssText());
        vars.put("cssUris", gfmConfig.getCssUris());
        vars.put("jsText", gfmConfig.getJsText());
        vars.put("jsUris", gfmConfig.getJsUris());
        String rendered = (String) TemplateRuntime.execute(htmlTemplate, vars);
        FileUtils.writeStringToFile(htFile, rendered);
    }

    @Override
    public String transformMarkdownText(String mdText) {
        if (clientConfig == null) {
            throw new IllegalStateException("missing setConfig");
        }
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
