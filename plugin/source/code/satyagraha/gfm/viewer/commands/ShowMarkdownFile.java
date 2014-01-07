package code.satyagraha.gfm.viewer.commands;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.ui.impl.ViewManager;
import code.satyagraha.gfm.viewer.model.api.MarkdownView;
import code.satyagraha.gfm.viewer.model.api.ViewerActions;

public class ShowMarkdownFile extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(ShowMarkdownFile.class.getPackage().getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.fine("");
        
        IStructuredSelection structuredSelection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        Object firstElement = structuredSelection.getFirstElement();
        if (firstElement instanceof IFile) {
            ViewManager.activateView(event, MarkdownView.ID);
            IFile iFile = (IFile) firstElement;
            try {
                DIManager.getDefault().getInjector(Scope.PAGE).getInstance(ViewerActions.class).showMarkdownFile(iFile);
            } catch (IOException e) {
                throw new ExecutionException("could not show file", e);
            }
        } else {
            LOGGER.fine("unexpected selection: " + firstElement);
        }
        return null;
    }

}
