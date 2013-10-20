package code.satyagraha.gfm.viewer.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import code.satyagraha.gfm.ui.impl.ViewLocator;
import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.views.api.ViewerActions;

public class GoForward extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Activator.debug("");
        ViewLocator.findViewImplementing(ViewerActions.class).goForward();
        return null;
    }

}
