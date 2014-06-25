package code.satyagraha.gfm.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;

import code.satyagraha.gfm.support.api.Config;
import code.satyagraha.gfm.support.api.WebProxyConfig;
import code.satyagraha.gfm.support.api.WebServiceClient;
import code.satyagraha.gfm.support.impl.WebServiceClientDefault;
import code.satyagraha.gfm.support.impl.WebServiceClientDefault.Markdown;
import code.satyagraha.gfm.support.impl.conn.ConnUtilities;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class TestWebServiceClientDefault {

    private static Logger LOGGER = Logger.getLogger(TestWebServiceClientDefault.class.getPackage().getName());

    // /////////////////////////////////////////////////////////////////////////

    @Path("/markdown")
    public static class StubResource {

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response createDataInJSON(Markdown markdown) {
            LOGGER.info("markdown: " + markdown);
            String result = performSampleTransformation(markdown.text);
            return Response.status(HttpStatus.CREATED_201).entity(result).type(MediaType.TEXT_HTML_TYPE).build();
        }

    }

    public static class StubResourceConfig extends DefaultResourceConfig {

        public StubResourceConfig() {
            super(StubResource.class);
            getClasses().addAll(ConnUtilities.getJerseyProviders());
            LOGGER.info("configuration complete");
        }
    }

    // /////////////////////////////////////////////////////////////////////////

    @Test
    public void shouldHandleSimpleRequest() throws Exception {
        ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter(ServletContainer.RESOURCE_CONFIG_CLASS, StubResourceConfig.class.getName());
        sh.setInitParameter(JSONConfiguration.FEATURE_POJO_MAPPING, "true");
        Server server = new Server(0);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(sh, "/*");

        server.start();
        
        Connector[] connectors = server.getConnectors();
        int port = connectors[0].getLocalPort();
        LOGGER.info("server port: "+ port);

        Config config = mock(Config.class);
        String apiUrl = "http://localhost:" + port;
        given(config.getApiUrl()).willReturn(apiUrl);

        WebProxyConfig webProxyConfig = mock(WebProxyConfig.class);

        ClientConnectionManager connectionManager = new SingleClientConnManager();

        WebServiceClient webServiceClient = new WebServiceClientDefault(config, webProxyConfig, connectionManager);

        String mdText = "Hello World!";
        String transformedText = webServiceClient.transform(mdText);

        assertThat(transformedText, is(performSampleTransformation(mdText)));

        server.stop();
    }

    private static String performSampleTransformation(String data) {
        return StringUtils.reverse(data);
    }
}
