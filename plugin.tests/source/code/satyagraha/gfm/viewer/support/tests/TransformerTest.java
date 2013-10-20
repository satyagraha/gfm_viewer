package code.satyagraha.gfm.viewer.support.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import code.satyagraha.gfm.support.api.Config;
import code.satyagraha.gfm.support.api.WebServiceClient;
import code.satyagraha.gfm.support.impl.TransformerDefault;

@RunWith(MockitoJUnitRunner.class)
public class TransformerTest {

    @Mock
    private Logger logger;

    @Mock
    private Config config;

    @Mock
    private WebServiceClient webServiceClient;

    @InjectMocks
    private TransformerDefault transformer;

    @Test
    public void shouldRecognizeKnownExtensions() {
        assertThat(transformer.markdownExtensions().contains("md"), is(true));
        assertThat(transformer.markdownExtensions().contains("markdown"), is(true));
    }
    
    @Test
    public void shouldCallWebServiceClient() {
        // given
        String mdText = "hello world";
        String htText = String.format("<p>%s</p>", mdText);
        given(webServiceClient.transform(mdText)).willReturn(htText);

        // when
        transformer.transformMarkdownText(mdText);

        // then
        verify(webServiceClient).transform(mdText);
    }

    @Test
    public void shouldIdentifyMarkdownFile() throws Exception {
        for (String extension : transformer.markdownExtensions()) {
            File mdFile = File.createTempFile("abc", "." + extension);
            assertThat(transformer.isMarkdownFile(mdFile), is(true));
        }
    }

    @Test
    public void shouldNotIdentifyFileWithoutExtension() throws Exception {
        File mdFile = File.createTempFile("abc", "");
        assertThat(transformer.isMarkdownFile(mdFile), is(false));
    }

    @Test
    public void shouldTransformExplicitMarkdownLink() {
        for (String extension : transformer.markdownExtensions()) {
            // given

            // when
            String resultLink = getTransformedLink("path/to/markdown." + extension);

            // then
            assertThat(resultLink, containsString(".md.html"));
        }
    }

    @Test
    public void shouldTransformImplicitMarkdownLink() {
        // given

        // when
        String resultLink = getTransformedLink("path/to/markdown");

        // then
        assertThat(resultLink, containsString(".md.html"));
    }

    @Test
    public void shouldNotTransformExplicitNonMarkdownLink() {
        // given

        // when
        String resultLink = getTransformedLink("path/to/something.htm");

        // then
        assertThat(resultLink, not(containsString(".html")));
    }

    private String getTransformedLink(String linkUri) {
        // given
        String htText = String.format("<a href=\"%s\">click me</a>", linkUri);
        given(webServiceClient.transform(anyString())).willReturn(htText);

        // when

        // then
        return transformer.transformMarkdownText("");
    }
}
