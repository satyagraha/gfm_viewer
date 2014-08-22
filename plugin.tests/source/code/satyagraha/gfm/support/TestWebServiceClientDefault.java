package code.satyagraha.gfm.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
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
import code.satyagraha.gfm.support.impl.conn.ClientConnManagerDefault;
import code.satyagraha.gfm.support.impl.conn.ConnUtilities;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class TestWebServiceClientDefault {

    private static Logger LOGGER = Logger.getLogger(TestWebServiceClientDefault.class.getPackage().getName());

    // /////////////////////////////////////////////////////////////////////////

    @Path("/markdown")
    public static class StubResource {

        private static final Random random = new Random();

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        public Response createDataInJSON(Markdown markdown) throws Exception {
            LOGGER.info("markdown: " + markdown);
            Thread.sleep(random.nextInt(50));
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

    public static class WebServiceClientCallable implements Callable<String> {

        private final WebServiceClient webServiceClient;
        private final String mdText;

        public WebServiceClientCallable(WebServiceClient webServiceClient, String mdText) {
            this.webServiceClient = webServiceClient;
            this.mdText = mdText;
        }

        @Override
        public String call() throws Exception {
            return webServiceClient.transform(mdText);
        }

    }

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
        LOGGER.info("server port: " + port);

        Config config = mock(Config.class);
        String apiUrl = "http://localhost:" + port;
        given(config.getApiUrl()).willReturn(apiUrl);

        WebProxyConfig webProxyConfig = mock(WebProxyConfig.class);
        SSLSocketFactory sslSocketFactory = mock(SSLSocketFactory.class);
        ClientConnectionManager connectionManager = new ClientConnManagerDefault(sslSocketFactory);
        WebServiceClient webServiceClient = new WebServiceClientDefault(config, webProxyConfig, connectionManager);

        Random random = new Random();

        // construct the tasks to run
        int threadCount = 50;
        List<WebServiceClientCallable> tasks = new ArrayList<WebServiceClientCallable>();
        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
            String mdText = RandomStringUtils.randomAlphanumeric(random.nextInt(100));
            WebServiceClientCallable task = new WebServiceClientCallable(webServiceClient, mdText);
            tasks.add(task);
        }

        // run the tasks
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = executorService.invokeAll(tasks, 10, TimeUnit.SECONDS);

        // verify results
        assertThat(futures, hasSize(threadCount));

        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
            String result = futures.get(threadIndex).get();
            WebServiceClientCallable task = tasks.get(threadIndex);
            assertThat(result, is(performSampleTransformation(task.mdText)));
        }

        server.stop();
    }

    private static String performSampleTransformation(String data) {
        return StringUtils.reverse(data);
    }
}
