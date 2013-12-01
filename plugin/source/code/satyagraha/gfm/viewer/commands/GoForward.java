package code.satyagraha.gfm.viewer.commands;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.viewer.model.api.ViewerActions;

public class GoForward extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(GoForward.class.getPackage().getName());

    @Inject
    private ViewerActions actions;

    public GoForward() {
        DIManager.getDefault().getInjector(Scope.PAGE).inject(this);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.fine("");
        actions.goForward();
        return null;
    }

}
