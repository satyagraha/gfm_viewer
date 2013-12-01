package code.satyagraha.gfm.viewer.model.impl;

import java.util.logging.Logger;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.viewer.model.api.ViewerSupport;

@Component(Scope.PAGE)
public class ViewerSupportDefault implements ViewerSupport {

    private static final String GFM_VIEWER_PLUGIN_LINKED = "GFM Viewer plugin.Linked";
    private static final String GFM_VIEWER_PLUGIN_ONLINE = "GFM Viewer plugin.Online";

    private static Logger LOGGER = Logger.getLogger(ViewerSupportDefault.class.getPackage().getName());
    
    private final ICommandService commandService;

    public ViewerSupportDefault(IWorkbenchPage page) {
        commandService = (ICommandService) page.getWorkbenchWindow().getService(ICommandService.class);
    }
    
    @Override
    public boolean isLinked() {
        LOGGER.fine("");
        Command command = commandService.getCommand(GFM_VIEWER_PLUGIN_LINKED);
        State state = command.getState(RegistryToggleState.STATE_ID);
        return (Boolean) state.getValue();
    }

    @Override
    public boolean isOnline() {
        LOGGER.fine("");
        Command command = commandService.getCommand(GFM_VIEWER_PLUGIN_ONLINE);
        State state = command.getState(RegistryToggleState.STATE_ID);
        return (Boolean) state.getValue();
    }
    
}
