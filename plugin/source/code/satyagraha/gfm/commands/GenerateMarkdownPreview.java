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
import code.satyagraha.gfm.viewer.views.GfmView;

public class GenerateMarkdownPreview extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Activator.debug("");
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);

        for (@SuppressWarnings("rawtypes") Iterator items = selection.iterator(); items.hasNext(); ) {
            Object item = items.next();
            if (item instanceof IFile) {
                GfmView.getInstance().generateIFile((IFile) item);
            } else if (item instanceof IFolder) {
                GfmView.getInstance().generateIFolder((IFolder) item);
            } else {
                Activator.debug("unexpected selection: " + item);
            }
        }
        
        return null;
    }

}
