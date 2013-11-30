package code.satyagraha.gfm.viewer.commands;

import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;


public class Linked extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(Linked.class.getPackage().getName());

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        // locate the view and perform action
        // update toggled state
        final Command command = event.getCommand();
        final boolean state = !HandlerUtil.toggleCommandState(command);
        LOGGER.fine("state: " + state);
        return null;
    }

}
