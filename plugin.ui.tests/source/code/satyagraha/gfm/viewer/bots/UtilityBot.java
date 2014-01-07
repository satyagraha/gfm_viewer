package code.satyagraha.gfm.viewer.bots;

import static org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory.withPartName;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.ListResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarPushButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarRadioButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarSeparatorButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarToggleButton;
import org.eclipse.ui.IViewSite;

public class UtilityBot {

    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    public static void setStandardKeyboard() {
        SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    }

    public static void closeWelcomeViews() {
        List<SWTBotView> welcomes = bot.views(withPartName("Welcome"));
        for (SWTBotView welcome : welcomes) {
            welcome.close();
        }
    }

    public static void resetWorkbench() {
        bot.resetWorkbench();
    }

    public static List<SWTBotToolbarButton> getToolbarButtons(final SWTBotView view) {
        return UIThreadRunnable.syncExec(new ListResult<SWTBotToolbarButton>() {

            @Override
            public List<SWTBotToolbarButton> run() {
                ToolBar toolbar = null;
                IToolBarManager t = ((IViewSite) view.getReference().getPart(false).getSite()).getActionBars().getToolBarManager();
                if (t instanceof ToolBarManager) {
                    toolbar = ((ToolBarManager) t).getControl();
                }

                final List<SWTBotToolbarButton> l = new ArrayList<SWTBotToolbarButton>();
                if (toolbar == null)
                    return l;

                for (ToolItem item : toolbar.getItems()) {
                    try {
                        if (SWTUtils.hasStyle(item, SWT.PUSH))
                            l.add(new SWTBotToolbarPushButton(item));
                        else if (SWTUtils.hasStyle(item, SWT.CHECK))
                            l.add(new SWTBotToolbarToggleButton(item));
                        else if (SWTUtils.hasStyle(item, SWT.RADIO))
                            l.add(new SWTBotToolbarRadioButton(item));
                        else if (SWTUtils.hasStyle(item, SWT.DROP_DOWN))
                            l.add(new SWTBotToolbarDropDownButton(item));
                        else if (SWTUtils.hasStyle(item, SWT.SEPARATOR))
                            l.add(new SWTBotToolbarSeparatorButton(item));
                    } catch (WidgetNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return l;
            }
        });
    }
    
    public static SWTBotToolbarButton getToolbarButtonWithTooltip(SWTBotView view, String text) {
        List<SWTBotToolbarButton> toolbarButtons = UtilityBot.getToolbarButtons(view);
        for (SWTBotToolbarButton toolbarButton : toolbarButtons) {
            if (StringUtils.equals(toolbarButton.getToolTipText(), text)) {
                return toolbarButton;
            }
        }
        throw new WidgetNotFoundException(text);
    }

}
