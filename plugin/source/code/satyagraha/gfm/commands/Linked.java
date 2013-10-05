package code.satyagraha.gfm.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

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

}
