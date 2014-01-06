package code.satyagraha.gfm.viewer.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import code.satyagraha.gfm.viewer.bots.EditorBot;
import code.satyagraha.gfm.viewer.bots.MarkdownViewBot;
import code.satyagraha.gfm.viewer.bots.PreferencesBot;
import code.satyagraha.gfm.viewer.bots.ProjectBot;
import code.satyagraha.gfm.viewer.bots.ProjectBot.ProjectFileBot;
import code.satyagraha.gfm.viewer.bots.UtilityBot;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MarkdownViewTest {

    private final static Logger LOGGER = Logger.getLogger(MarkdownViewTest.class.getPackage().getName());
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        LOGGER.info("commencing tests");
        
        UtilityBot.closeWelcomeViews();
        PreferencesBot.setApiUrl("");
    }

    @Before
    public void before() throws Exception {
        // no-op
    }
    
    @After
    public void after() throws Exception {
        SWTUtils.sleep(2000);
    }
    
    @AfterClass
    public static void afterClass() {
        // no-op
    }

    @Test
    public void shouldOpenThenCloseViewTwice() throws Exception {
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

    @Test
    public void shouldUpdateViewWhenEditorOpened() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot markdownViewBot1 = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));

        ProjectBot project1 = ProjectBot.createSimpleProject();
        project1.newFile("file1.md");
        
        SWTUtils.sleep(2000);
        
        assertThat(markdownViewBot1.getTitle(), is("file1"));
        markdownViewBot1.close();
        
        EditorBot.closeAll();
        project1.delete();
    }
    
    @Test
    public void shouldFollowEditorSelected() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        
        ProjectBot project1 = ProjectBot.createSimpleProject();
        project1.newFile("file1.md");
        project1.newFile("file2.md");
                
        MarkdownViewBot markdownViewBot = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));
        
        EditorBot editorBot1 = EditorBot.findByName("file1.md").show();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file1"));
        
        EditorBot editorBot2 = EditorBot.findByName("file2.md").show();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file2"));
        
        editorBot2.close();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file1"));
        
        editorBot1.close();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file1"));
        
        markdownViewBot.close();
        
        EditorBot.closeAll();
        project1.delete();
    }
    
    @Test
    public void shouldSupportMarkdownFileContextMenuToView() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        
        ProjectBot project1 = ProjectBot.createSimpleProject();
        ProjectFileBot fileBot1 = project1.newFile("file1.md");
        EditorBot.findByName("file1.md").close();
        
        fileBot1.showInGfmView();
        SWTUtils.sleep(2000);
        
        assertThat(MarkdownViewBot.isPresent(), is(true));
        MarkdownViewBot markdownViewBot = MarkdownViewBot.findById();
        assertThat(markdownViewBot.getTitle(), is("file1"));
        
        markdownViewBot.close();
        
        EditorBot.closeAll();
        project1.delete();
    }

    @Test
    public void shouldGenerateHTMLviaContextMenu() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        
        ProjectBot project1 = ProjectBot.createSimpleProject();
        ProjectFileBot fileBot1 = project1.newFile("file1.md");
        EditorBot.findByName("file1.md").close();
        
        File file1md = fileBot1.asIFile().getRawLocation().toFile();
        File file1ht = new File(file1md.getParentFile(), ".file1.md.html");
        assertThat(file1ht.exists(), is(false));
        
        fileBot1.generateMarkdownPreview();
        SWTUtils.sleep(2000);
        assertThat("not found: " + file1ht, file1ht.exists(), is(true));
        
        EditorBot.closeAll();
        project1.delete();
    }
    
}
