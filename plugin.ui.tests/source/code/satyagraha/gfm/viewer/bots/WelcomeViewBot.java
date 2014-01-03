package code.satyagraha.gfm.viewer.bots;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withPartName;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;

public class WelcomeViewBot {
    
    public static void closeAll() {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        List<SWTBotView> welcomes = bot.views(withPartName("Welcome"));
        for (SWTBotView welcome : welcomes) {
            welcome.close();
        }
    }

}
