package code.satyagraha.gfm.viewer.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import code.satyagraha.gfm.viewer.bots.MarkdownViewBot;
import code.satyagraha.gfm.viewer.bots.WelcomeViewBot;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MarkdownViewTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        WelcomeViewBot.closeAll();
    }

    @Test
    public void canOpenThenCloseViewTwice() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot markdownViewBot1 = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));
        markdownViewBot1.close();
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot markdownViewBot2 = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));
        markdownViewBot2.close();
        assertThat(MarkdownViewBot.isPresent(), is(false));
    }

    @AfterClass
    public static void afterClass() {
        SWTUtils.sleep(1000);
    }

}
