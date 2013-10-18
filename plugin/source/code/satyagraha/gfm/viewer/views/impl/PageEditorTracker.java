package code.satyagraha.gfm.viewer.views.impl;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;

import code.satyagraha.gfm.viewer.views.api.EditorPartListener;

public class PageEditorTracker implements IPartListener2 {

    private final IWorkbenchPage workbenchPage;
    private EditorPartListener listener;

    public PageEditorTracker(IWorkbenchPage workbenchPage) {
        this.workbenchPage = workbenchPage;
    }

    public void subscribe(EditorPartListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        } else if (this.listener != null) {
            throw new IllegalStateException();
        } else {
            this.listener = listener;
            workbenchPage.addPartListener(this);
        }
    }

    public void unsubscribe(EditorPartListener listener) {
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
        checkShown(partRef);
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        checkShown(partRef);
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        checkClosed(partRef);
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        // no-op
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        // no-op
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        // no-op
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        checkShown(partRef);
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
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
