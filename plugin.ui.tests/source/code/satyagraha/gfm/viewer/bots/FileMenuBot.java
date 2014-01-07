package code.satyagraha.gfm.viewer.bots;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class FileMenuBot {
    
    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    public static void newFile(String filename) {
        bot.menu("File").menu("New").menu("File").click();
        SWTBotShell shell = bot.shell("New File");
        shell.activate();
        bot.textWithLabel("File name:").setText(filename);
        bot.button("Finish").click();
    }

}
