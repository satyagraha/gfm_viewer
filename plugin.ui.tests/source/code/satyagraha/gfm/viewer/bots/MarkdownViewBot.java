package code.satyagraha.gfm.viewer.bots;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withPartId;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class MarkdownViewBot {

    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();
    
    private final static String gfmViewId = "code.satyagraha.gfm.viewer.views.GfmView";
    
    public static boolean isPresent() {
        return !getGfmViews().isEmpty();
    }
    
    public static MarkdownViewBot open() {
        bot.menu("Window").menu("Show View").menu("Other...").click();
        SWTBotShell shell = bot.shell("Show View");
        shell.activate();
        bot.tree().expandNode("GFM Support").select("GFM View");
        bot.button("OK").click();
        return findById();
    }

    public static MarkdownViewBot findById() {
        SWTBotView gfmView = bot.viewById(gfmViewId); // N.B. waits!
        return new MarkdownViewBot(gfmView);
    }
    
//    public static MarkdownViewBot find(String viewName) {
//        SWTBotView gfmView = bot.viewByTitle(viewName); // N.B. waits!
//        return new MarkdownViewBot(gfmView);
//    }
    
    private static List<SWTBotView> getGfmViews() {
        return bot.views(withPartId(gfmViewId));
    }

    ///////////////////////////////////////////////////////////////////////////
    
    private SWTBotView gfmView;
    
    private MarkdownViewBot(SWTBotView gfmView) {
        this.gfmView = gfmView;
    }
    
    public String getTitle() {
        return gfmView.getReference().getPart(false).getTitle();
    }
    
    public void close() {
        gfmView.close();
        gfmView = null;
    }
    
}
