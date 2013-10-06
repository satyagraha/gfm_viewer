package code.satyagraha.gfm.commands;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.views.api.Scheduler;

public class GenerateMarkdownPreview extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Activator.debug("");
        
        Scheduler scheduler = Activator.getDefault().getInjector().getInstance(Scheduler.class);
        
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);
        for (@SuppressWarnings("rawtypes") Iterator items = selection.iterator(); items.hasNext(); ) {
            Object item = items.next();
            if (item instanceof IFile) {
                scheduler.generateIFile((IFile) item);
            } else if (item instanceof IFolder) {
                scheduler.generateIFolder((IFolder) item);
            } else {
                Activator.debug("unexpected selection: " + item);
            }
        }
        return null;
    }

}
