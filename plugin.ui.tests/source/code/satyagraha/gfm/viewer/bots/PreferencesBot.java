package code.satyagraha.gfm.viewer.bots;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class PreferencesBot {

    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();
    
    private SWTBotShell botShell;

    public static PreferencesBot activateFromWorkbench() {
        bot.menu("Window").menu("Preferences").click();
        return fromActivatedPreferences();
    }
    
    static PreferencesBot fromActivatedPreferences() {
        SWTBotShell botShell = bot.shell("Preferences");
        botShell.activate();
        return new PreferencesBot(botShell);
    }

    public PreferencesBot(SWTBotShell botShell) {
        this.botShell = botShell;
    }
    
    public void setApiUrl(String apiUrl) {
        botShell.bot().tree().getTreeItem("GFM Viewer").select().click();
        botShell.bot().textWithLabel("API URL:").setText(apiUrl);
        botShell.bot().button("Apply").click();
        botShell.bot().button("OK").click();
    }

    public void cancel() {
        botShell.bot().button("Cancel").click();
    }

}
