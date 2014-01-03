package code.satyagraha.gfm.viewer.bots;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withPartName;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class MarkdownViewBot {

    private static SWTWorkbenchBot bot;
    
    private SWTBotView gfmView;
    
    private MarkdownViewBot(SWTBotView gfmView) {
        this.gfmView = gfmView;
    }
    
    public static boolean isPresent() {
        return !getGfmViews().isEmpty();
    }
    
    public static MarkdownViewBot open() {
        bot.menu("Window").menu("Show View").menu("Other...").click();
        SWTBotShell shell = bot.shell("Show View");
        shell.activate();
        bot.tree().expandNode("GFM Support").select("GFM View");
        bot.button("OK").click();
        SWTBotView gfmView = bot.viewByTitle("GFM View"); //          getGfmViews().get(0);
        return new MarkdownViewBot(gfmView);
    }
    
    public void close() {
        gfmView.close();
    }
    
    private static SWTWorkbenchBot getBot() {
        if (bot == null) {
            bot = new SWTWorkbenchBot();
        }
        return bot;
    }
    
    private static List<SWTBotView> getGfmViews() {
        return getBot().views(withPartName("GFM View"));
    }
    
}
