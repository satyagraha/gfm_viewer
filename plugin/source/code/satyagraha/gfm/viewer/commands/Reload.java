package code.satyagraha.gfm.viewer.commands;

import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import code.satyagraha.gfm.ui.impl.ViewLocator;
import code.satyagraha.gfm.viewer.views.api.ViewerActions;

public class Reload extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(Reload.class.getPackage().getName());

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        LOGGER.fine("");
        // locate the view and perform action
        ViewLocator.findViewImplementing(ViewerActions.class).reload();
        return null;
    }

}
