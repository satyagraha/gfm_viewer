package code.satyagraha.gfm.viewer.commands;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import code.satyagraha.gfm.ui.impl.ViewLocator;
import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.views.api.MarkdownListener;
import code.satyagraha.gfm.viewer.views.impl.MarkdownView;

public class ShowMarkdownFile extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Activator.debug("");
        
        IStructuredSelection structuredSelection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        Object firstElement = structuredSelection.getFirstElement();
        if (firstElement instanceof IFile) {
            IFile iFile = (IFile) firstElement;
            try {
                IViewPart view = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(MarkdownView.ID);
                HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().activate(view);
            } catch (PartInitException e) {
                throw new ExecutionException("failed to show view", e);
            }
            try {
                ViewLocator.findViewImplementing(MarkdownListener.class).showIFile(iFile);
            } catch (IOException e) {
                throw new ExecutionException("could not show file", e);
            }
        } else {
            Activator.debug("unexpected selection: " + firstElement);
        }
        return null;
    }

}
