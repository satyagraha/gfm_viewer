package code.satyagraha.gfm.viewer.commands;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.viewer.model.api.ViewerActions;

public class GoBackward extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(GoBackward.class.getPackage().getName());

    @Inject
    private ViewerActions actions;

    public GoBackward() {
        DIManager.getDefault().getInjector(Scope.PAGE).inject(this);
    }
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.fine("");
        actions.goBackward();
        return null;
    }

}
