package code.satyagraha.gfm.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import code.satyagraha.gfm.support.api.Config;
import code.satyagraha.gfm.support.api.WebServiceClient;
import code.satyagraha.gfm.support.impl.ConfigDefault;
import code.satyagraha.gfm.support.impl.TransformerDefault;

@RunWith(MockitoJUnitRunner.class)
public class TransformerTest {

    @Spy
    private Config config = new ConfigDefault();

    @Mock
    private WebServiceClient webServiceClient;

    @InjectMocks
    private TransformerDefault transformer;

    @Test
    public void shouldRecognizeKnownExtensions() {
        assertThat(transformer.markdownExtensions().contains("md"), is(true));
        assertThat(transformer.markdownExtensions().contains("mdown"), is(true));
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

    @Test
    public void shouldNotTransformSimpleFragmentLink() {
        // given

        // when
        String resultLink = getTransformedLink("#localref");

        // then
        assertThat(resultLink, not(containsString(".html")));
        
    }
    
    @Test
    public void shouldTransformGitHubAnchor() {
        // given
        String anchorText = "<a name=\"user-content-todo\" class=\"anchor\" href=\"#TODO\">stuff</a>";
        
        // when
        String resultText = getTransformedText(anchorText);
        
        // then
        assertThat(resultText, is(anchorText.replace("user-content-", "")));
    }
    
    @Test
    public void shouldNotTransformNonGitHubAnchor() {
        // given
        String anchorText = "<a name=\"todo\" class=\"anchor\" href=\"#todo\">stuff</a>";
        
        // when
        String resultText = getTransformedText(anchorText);
        
        // then
        assertThat(resultText, is(anchorText));
    }

    @Test
    public void shouldWorkWithEmptyCssJs() throws Exception {
        // setup
        transformer = new TransformerDefault(config, webServiceClient);

        // given
        File mdFile = File.createTempFile("src", ".md");
        String message = "hello world - " + System.currentTimeMillis(); 
        String mdText = message + "\n";
        FileUtils.write(mdFile, mdText);
        
        File htFile = File.createTempFile("dst", ".md");
        String htText = String.format("<p>%s</p>", message);
        given(webServiceClient.transform(mdText)).willReturn(htText);
        given(config.getCssText()).willReturn(null);
        given(config.getJsText()).willReturn(null);
        
        // when
        transformer.transformMarkdownFile(mdFile, htFile);
        
        // then
        String resultText = FileUtils.readFileToString(htFile);
        assertThat(resultText, containsString(message));
    }
    
    private String getTransformedLink(String linkUri) {
        String htText = String.format("<a href=\"%s\">click me</a>", linkUri);
        return getTransformedText(htText);
    }        
    
    private String getTransformedText(String htText) {
        given(webServiceClient.transform(anyString())).willReturn(htText);

        // when

        // then
        return transformer.transformMarkdownText("");
    }
}
