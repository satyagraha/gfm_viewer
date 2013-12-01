package code.satyagraha.gfm.viewer.commands;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.viewer.model.api.ViewerActions;

public class Reload extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(Reload.class.getPackage().getName());

    @Inject
    private ViewerActions actions;

    public Reload() {
        DIManager.getDefault().getInjector(Scope.PAGE).inject(this);
    }

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        LOGGER.fine("");
        actions.reload();
        return null;
    }

}
