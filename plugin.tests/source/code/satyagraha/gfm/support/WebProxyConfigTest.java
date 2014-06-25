package code.satyagraha.gfm.support;

import static org.eclipse.core.net.proxy.IProxyData.HTTP_PROXY_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.net.URI;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import code.satyagraha.gfm.support.api.WebProxyConfig;
import code.satyagraha.gfm.support.api.WebProxyConfig.WebProxyData;
import code.satyagraha.gfm.support.impl.WebProxyConfigDefault;

@RunWith(MockitoJUnitRunner.class)
public class WebProxyConfigTest {

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference<IProxyService> proxyServiceReference;

    @Mock
    private IProxyService proxyService;

    private WebProxyConfig webProxyConfig;

    IProxyData proxyData = mock(IProxyData.class);

    private IProxyData[] proxyDataListEmpty = {};

    private IProxyData[] proxyDataListOne = { proxyData };

    @Before
    public void setup() {
        given(bundleContext.getServiceReference(IProxyService.class)).willReturn(proxyServiceReference);
        given(bundleContext.getService(proxyServiceReference)).willReturn(proxyService);
        webProxyConfig = new WebProxyConfigDefault(bundleContext);
    }

    @Test
    public void shouldHandleNoMatchingProxy() throws Exception {
        // given
        URI uri = new URI("http://www.test");
        given(proxyService.select(uri)).willReturn(proxyDataListEmpty);

        // when
        WebProxyData webProxyData = webProxyConfig.getWebProxyData(uri);

        // then
        assertThat(webProxyData, nullValue());
    }

    @Test
    public void shouldHandleMatchingProxy() throws Exception {
        // given
        given(proxyData.getType()).willReturn(HTTP_PROXY_TYPE);
        String proxyHost = "proxyHost";
        given(proxyData.getHost()).willReturn(proxyHost);
        int proxyPort = 1234;
        given(proxyData.getPort()).willReturn(proxyPort);
        String userid = "userid";
        given(proxyData.getUserId()).willReturn(userid);
        String password = "password";
        given(proxyData.getPassword()).willReturn(password);
        URI uri = new URI("http://www.test");
        given(proxyService.select(uri)).willReturn(proxyDataListOne);

        // when
        WebProxyData webProxyData = webProxyConfig.getWebProxyData(uri);

        // then
        assertThat(webProxyData, notNullValue());
        URI proxyUri = new URI(webProxyData.getProxyUri());
        assertThat(proxyUri.getHost(), is(proxyHost));
        assertThat(proxyUri.getPort(), is(proxyPort));
        assertThat(webProxyData.getUserId(), is(userid));
        assertThat(webProxyData.getPassword(), is(password));
    }

}
