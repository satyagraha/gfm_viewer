package code.satyagraha.gfm.ui.impl;

import java.util.logging.Logger;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.ui.api.EditorPartListener;
import code.satyagraha.gfm.ui.api.PageEditorTracker;

@Component(Scope.PAGE)
public class PageEditorTrackerDefault implements PageEditorTracker {

    private static Logger LOGGER = Logger.getLogger(PageEditorTrackerDefault.class.getPackage().getName());

    private final IWorkbenchPage workbenchPage;
    private EditorPartListener listener;

    public PageEditorTrackerDefault(IWorkbenchPage workbenchPage) {
        this.workbenchPage = workbenchPage;
        LOGGER.fine("");
    }

    /* (non-Javadoc)
     * @see code.satyagraha.gfm.viewer.ui.impl.PageEditorTrackerApi#subscribe(code.satyagraha.gfm.support.impl.EditorPartListener)
     */
    @Override
    public void subscribe(EditorPartListener listener) {
        LOGGER.fine("listener: " + listener);
        if (listener == null) {
            throw new NullPointerException();
        } else if (this.listener != null) {
            throw new IllegalStateException();
        } else {
            this.listener = listener;
            workbenchPage.addPartListener(this);
        }
    }

    /* (non-Javadoc)
     * @see code.satyagraha.gfm.viewer.ui.impl.PageEditorTrackerApi#unsubscribe(code.satyagraha.gfm.support.impl.EditorPartListener)
     */
    @Override
    public void unsubscribe(EditorPartListener listener) {
        LOGGER.fine("listener: " + listener);
        if (this.listener != null) {
            if (this.listener != listener) {
                throw new IllegalStateException();
            } else {
                this.listener = null;
                workbenchPage.removePartListener(this);
            }
        }
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        checkShown(partRef);
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        checkShown(partRef);
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        checkClosed(partRef);
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        // no-op
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        // no-op
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        // no-op
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        checkShown(partRef);
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        LOGGER.fine("");
        checkShown(partRef);
    }

    private void checkShown(IWorkbenchPartReference partRef) {
        if (partRef.getPage() == workbenchPage && partRef instanceof IEditorReference) {
            listener.editorShown(((IEditorReference) partRef).getEditor(true));
        }
    }

    private void checkClosed(IWorkbenchPartReference partRef) {
        if (partRef.getPage() == workbenchPage && partRef instanceof IEditorReference) {
            listener.editorClosed(((IEditorReference) partRef).getEditor(true));
        }
    }

}
