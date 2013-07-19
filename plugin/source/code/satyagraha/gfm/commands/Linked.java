package code.satyagraha.gfm.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.views.GfmView;


public class Linked extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {

        // locate the view and perform action
        // update toggled state
        final Command command = event.getCommand();
        final boolean state = !HandlerUtil.toggleCommandState(command);
        Activator.debug("state: " + state);
        GfmView.getInstance().setLinkedState(state);

        return null;
    }

    public static boolean isLinked() {
        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(ICommandService.class);
        Command command = commandService.getCommand("GFM Viewer plugin.Linked");
        State state = command.getState(RegistryToggleState.STATE_ID);
        return (Boolean) state.getValue();
    }
}
