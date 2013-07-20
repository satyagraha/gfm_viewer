package code.satyagraha.gfm.viewer.views;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.ResourceUtil;

import code.satyagraha.gfm.support.api.FileNature;
import code.satyagraha.gfm.viewer.plugin.Activator;

public class EditorTracker implements IPartListener2 {

    private IWorkbenchWindow workbenchWindow;
    private GfmListener gfmListener;
    private FileNature fileNature;
    
    private boolean notificationsEnabled;
    private IFile gfmFile;

    public EditorTracker(IWorkbenchWindow workbenchWindow, GfmListener gfmListener, FileNature fileNature) {
        this.workbenchWindow = workbenchWindow;
        this.gfmListener = gfmListener;
        this.fileNature = fileNature;
        notificationsEnabled = true;
        Activator.debug("");
        workbenchWindow.getPartService().addPartListener(this);
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        Activator.debug("notificationsEnabled: " + notificationsEnabled);
        this.notificationsEnabled = notificationsEnabled;
    }
    
    public void notifyGfmListenerAlways() {
        Activator.debug("");
        if (gfmListener != null) {
            try {
                gfmListener.showIFile(gfmFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void close() {
        Activator.debug("");
        workbenchWindow.getPartService().removePartListener(this);
        workbenchWindow = null;
        gfmListener = null;
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        Activator.debug("");
        checkIfTrackable(partRef);
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        Activator.debug("");
        checkIfTrackable(partRef);
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        Activator.debug("");
        checkIfUntrackable(partRef);
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        Activator.debug("");
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        Activator.debug("");
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        Activator.debug("");
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        Activator.debug("");
        checkIfTrackable(partRef);
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        Activator.debug("");
        checkIfTrackable(partRef);
    }

    private void checkIfTrackable(IWorkbenchPartReference partRef) {
        Activator.debug("");
        IWorkbenchPart part = partRef.getPart(true);
        if (part instanceof IEditorPart) {
            final IEditorPart editorPart = (IEditorPart) part;
            IEditorInput editorInput = editorPart.getEditorInput();
            final IFile editorFile = ResourceUtil.getFile(editorInput);
            if (fileNature.isTrackableFile(editorFile) && isNewFile(editorFile)) {
                Activator.debug("opening markdown editor found");
                gfmFile = editorFile;
                notifyGfmListenerIfEnabled();
                editorPart.addPropertyListener(new IPropertyListener() {

                    @Override
                    public void propertyChanged(Object source, int propId) {
                        Activator.debug(String.format("%s => %d", source, propId));
                        if (propId == IEditorPart.PROP_DIRTY && !editorPart.isDirty()) {
                            notifyGfmListenerIfEnabled();
                        }
                    }
                });
            }
        }
    }

    private void checkIfUntrackable(IWorkbenchPartReference partRef) {
        Activator.debug("");
        IWorkbenchPart part = partRef.getPart(true);
        if (part instanceof IEditorPart) {
            final IEditorPart editorPart = (IEditorPart) part;
            IEditorInput editorInput = editorPart.getEditorInput();
            final IFile editorFile = ResourceUtil.getFile(editorInput);
            if (fileNature.isTrackableFile(editorFile) && isSameFile(editorFile)) {
                Activator.debug("closing markdown editor found");
                gfmFile = null;
                notifyGfmListenerIfEnabled();
            }
        }
    }

    private boolean isNewFile(IFile editorFile) {
        return gfmFile == null || !gfmFile.getFullPath().equals(editorFile.getFullPath());
    }

    private boolean isSameFile(IFile editorFile) {
        return gfmFile != null && gfmFile.getFullPath().equals(editorFile.getFullPath());
    }

    private void notifyGfmListenerIfEnabled() {
        Activator.debug("");
        if (notificationsEnabled) {
            notifyGfmListenerAlways();
        }
    }
    
}
