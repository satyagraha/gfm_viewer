package code.satyagraha.gfm.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import code.satyagraha.gfm.viewer.views.GfmView;


public class Reload extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        // Activator.debug("Update");

        // locate the view and perform action
        GfmView.getInstance().reload();

        return null;
    }

}
