package code.satyagraha.gfm.viewer.bots;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;

public class EditorBot {

    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    private SWTBotEditor swtBotEditor;

    public static EditorBot findByName(String fileName) {
        SWTBotEditor swtBotEditor = bot.editorByTitle(fileName);
        return new EditorBot(swtBotEditor);
    }
    
    public static void closeAll() {
        bot.closeAllEditors();
    }
    
    public static void saveAll() {
        bot.saveAllEditors();
    }
    
    public EditorBot(SWTBotEditor swtBotEditor) {
        this.swtBotEditor = swtBotEditor;
    }
    
    public EditorBot show() {
        swtBotEditor.show();
        return this;
    }
    
    public EditorBot typeText(String text) {
        swtBotEditor.toTextEditor().typeText(text);
        return this;
    }

    public EditorBot save() {
        swtBotEditor.save();
        return this;
    }
    
    public void close() {
        swtBotEditor.close();
    }

}
