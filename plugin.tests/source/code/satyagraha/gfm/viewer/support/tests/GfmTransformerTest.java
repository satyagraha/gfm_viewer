package code.satyagraha.gfm.viewer.support.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.support.api.GfmWebServiceClient;
import code.satyagraha.gfm.support.impl.GfmTransformerDefault;

@RunWith(MockitoJUnitRunner.class)
public class GfmTransformerTest {

    @Mock
    private Logger logger;

    @Mock
    private GfmConfig gfmConfig;
    
    @Mock
    private GfmWebServiceClient webServiceClient;
    
    @InjectMocks
    private GfmTransformerDefault gfmTransformer;
    
    @Test
    public void shouldCallWebServiceClient() {
        // given
        String mdText = "hello world";
        String htText = String.format("<p>%s</p>", mdText);
        given(webServiceClient.transform(mdText)).willReturn(htText);
        
        // when
        gfmTransformer.transformMarkdownText(mdText);
        
        // then
        verify(webServiceClient).transform(mdText);
    }
    
    @Test
    public void shouldIdentifyMarkdownFile() throws Exception {
        File mdFile = File.createTempFile("abc", ".md");
        assertThat(gfmTransformer.isMarkdownFile(mdFile), is(true));
    }
    
}
