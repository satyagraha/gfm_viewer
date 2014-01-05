package code.satyagraha.gfm.viewer.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import code.satyagraha.gfm.viewer.bots.MarkdownViewBot;
import code.satyagraha.gfm.viewer.bots.PreferencesBot;
import code.satyagraha.gfm.viewer.bots.ProjectBot;
import code.satyagraha.gfm.viewer.bots.ProjectBot.ProjectFileBot;
import code.satyagraha.gfm.viewer.bots.UtilityBot;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MarkdownViewTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        UtilityBot.closeWelcomeViews();
        PreferencesBot.setApiUrl("");
    }

    @Before
    public void before() throws Exception {
//        UtilityBot.resetWorkbench();
    }
    
    @Test
//    @Ignore
    public void canOpenThenCloseViewTwice() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot.open();
        MarkdownViewBot markdownViewBot1 = MarkdownViewBot.find("GFM View");
        assertThat(MarkdownViewBot.isPresent(), is(true));
        markdownViewBot1.close();
        
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot.open();
        MarkdownViewBot markdownViewBot2 = MarkdownViewBot.find("GFM View");
        assertThat(MarkdownViewBot.isPresent(), is(true));
        markdownViewBot2.close();
        
        assertThat(MarkdownViewBot.isPresent(), is(false));
    }

    @Test
    public void shouldUpdateViewWhenEditorOpened() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot.open();
        MarkdownViewBot markdownViewBot1 = MarkdownViewBot.find("GFM View");
        assertThat(MarkdownViewBot.isPresent(), is(true));

        ProjectBot project1 = ProjectBot.createSimpleProject("Project_1");
        ProjectFileBot file1 = project1.newFile("file1.md");
        
        SWTUtils.sleep(2000);
        
        assertThat(markdownViewBot1.getTitle(), is("file1"));
        markdownViewBot1.close();
    }
    
    
    @AfterClass
    public static void afterClass() {
        SWTUtils.sleep(1000);
    }

}
