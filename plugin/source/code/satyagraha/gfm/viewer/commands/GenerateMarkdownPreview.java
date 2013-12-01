package code.satyagraha.gfm.viewer.commands;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.ui.api.Scheduler;

public class GenerateMarkdownPreview extends AbstractHandler {

    private static Logger LOGGER = Logger.getLogger(GenerateMarkdownPreview.class.getPackage().getName());

    @Inject
    private Scheduler scheduler;

    public GenerateMarkdownPreview() {
        DIManager.getDefault().getInjector(Scope.PLUGIN).inject(this);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        LOGGER.fine("");
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        for (@SuppressWarnings("rawtypes")
        Iterator items = selection.iterator(); items.hasNext();) {
            Object item = items.next();
            if (item instanceof IFile) {
                scheduler.generateIFile((IFile) item);
            } else if (item instanceof IFolder) {
                scheduler.generateIFolder((IFolder) item);
            } else {
                LOGGER.fine("unexpected selection: " + item);
            }
        }
        return null;
    }

}
