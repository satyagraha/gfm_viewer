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
    public void canOpenView() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot markdownViewBot = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));
    }

    @AfterClass
    public static void afterClass() {
        SWTUtils.sleep(1000);
    }

}
