package code.satyagraha.gfm.viewer.bots;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class PreferencesBot {

    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    public static void setApiUrl(String apiUrl) {
        bot.menu("Window").menu("Preferences").click();
        SWTBotShell shell = bot.shell("Preferences");
        shell.activate();
//        SWTBotTreeItem treeItem =
        bot.tree().getTreeItem("GFM Viewer").select().click();
        bot.textWithLabel("API URL:").setText(apiUrl);
        bot.button("Apply").click();
        bot.button("OK").click();
    }

}
