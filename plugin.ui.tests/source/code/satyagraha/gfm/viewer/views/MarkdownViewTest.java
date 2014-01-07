package code.satyagraha.gfm.viewer.views;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
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

        UtilityBot.setStandardKeyboard();
        UtilityBot.closeWelcomeViews();
        PreferencesBot.activateFromWorkbench().setApiUrl("");
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
    public void shouldFollowEditorSelectedWhenLinked() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));

        ProjectBot project = ProjectBot.createSimpleProject();
        project.newFile("file1.md");
        project.newFile("file2.md");

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
        project.delete();
    }

    @Test
    public void shouldNotFollowEditorSelectedWhenNotLinked() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));

        ProjectBot project = ProjectBot.createSimpleProject();
        project.newFile("file1.md");
        project.newFile("file2.md");

        MarkdownViewBot markdownViewBot = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));
        SWTBotToolbarButton linkedButton = markdownViewBot.getLinkedButton();

        EditorBot editorBot1 = EditorBot.findByName("file1.md").show();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file1"));

        EditorBot editorBot2 = EditorBot.findByName("file2.md").show();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file2"));

        linkedButton.click(); // set unlinked

        editorBot2.close();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file2"));

        editorBot1.close();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file2"));

        linkedButton.click();
        markdownViewBot.close();
        project.delete();
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

        ProjectBot project = ProjectBot.createSimpleProject();
        ProjectFileBot fileBot = project.newFile("file1.md");
        EditorBot editorBot = EditorBot.findByName("file1.md");

        String textMd = "sample text, timestamp: " + System.currentTimeMillis() + ".";
        editorBot.typeText(textMd + "\r");
        editorBot.save();
        editorBot.close();

        File fileMd = fileBot.toFile();
        File fileHt = new File(fileMd.getParentFile(), ".file1.md.html");
        assertThat(fileHt.exists(), is(false));

        fileBot.generateMarkdownPreview();
        SWTUtils.sleep(2000);
        assertThat("not found: " + fileHt, fileHt.exists(), is(true));

        String textHt = IOUtils.toString(new FileInputStream(fileHt));
        assertThat(textHt, containsString(textMd));

        EditorBot.closeAll();
        project.delete();
    }

    @Test
    public void shouldGenerateHTMLonEditorSave() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot markdownViewBot = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));

        ProjectBot project = ProjectBot.createSimpleProject();
        ProjectFileBot fileBot = project.newFile("file1.md");
        EditorBot editorBot = EditorBot.findByName("file1.md");

        String textMd = "sample text, timestamp: " + System.currentTimeMillis() + ".";
        editorBot.typeText(textMd + "\r");
        editorBot.save();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("file1"));

        File fileMd = fileBot.toFile();
        File fileHt = new File(fileMd.getParentFile(), ".file1.md.html");
        assertThat("not found: " + fileHt, fileHt.exists(), is(true));

        String textHt = IOUtils.toString(new FileInputStream(fileHt));
        assertThat(textHt, containsString(textMd));

        editorBot.close();
        markdownViewBot.close();
        project.delete();
    }

    @Test
    public void shouldNotGenerateHTMLonEditorSaveIfOffline() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot markdownViewBot = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));

        SWTBotToolbarButton onlineButton = markdownViewBot.getOnlineButton();

        ProjectBot project = ProjectBot.createSimpleProject();
        ProjectFileBot fileBot = project.newFile("file1.md");
        EditorBot editorBot = EditorBot.findByName("file1.md");

        String textMd1 = "sample text 1, timestamp: " + System.currentTimeMillis() + ".";
        editorBot.typeText(textMd1 + "\r");
        editorBot.save();

        onlineButton.click(); // set offline

        String textMd2 = "sample text 2, timestamp: " + System.currentTimeMillis() + ".";
        editorBot.typeText(textMd1 + "\r");
        editorBot.save();
        SWTUtils.sleep(2000);
        assertThat(markdownViewBot.getTitle(), is("*file1"));

        File fileMd = fileBot.toFile();
        File fileHt = new File(fileMd.getParentFile(), ".file1.md.html");
        assertThat("not found: " + fileHt, fileHt.exists(), is(true));

        String textHt = IOUtils.toString(new FileInputStream(fileHt));
        assertThat(textHt, containsString(textMd1));
        assertThat(textHt, not(containsString(textMd2)));

        editorBot.close();
        onlineButton.click();
        markdownViewBot.close();
        project.delete();
    }

    @Test
    public void shouldShowPreferencesViaToolbar() throws Exception {
        assertThat(MarkdownViewBot.isPresent(), is(false));
        MarkdownViewBot markdownViewBot1 = MarkdownViewBot.open();
        assertThat(MarkdownViewBot.isPresent(), is(true));
        SWTUtils.sleep(2000);

        PreferencesBot preferencesBot = markdownViewBot1.showPreferences();
        preferencesBot.cancel();

        markdownViewBot1.close();
    }

}
