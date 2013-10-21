package code.satyagraha.gfm.viewer.views.impl;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ide.ResourceUtil;

import code.satyagraha.gfm.support.api.FileNature;
import code.satyagraha.gfm.ui.api.EditorPartListener;
import code.satyagraha.gfm.ui.api.PageEditorTracker;
import code.satyagraha.gfm.viewer.views.api.MarkdownListener;

public class MarkdownEditorTracker implements EditorPartListener {

    private static int instances = 0;
    private static Logger LOGGER = Logger.getLogger(MarkdownEditorTracker.class.getPackage().getName());

    private final int instance;
    private MarkdownListener markdownListener;
    private FileNature fileNature;
    private PageEditorTracker pageEditorTracker;
    private boolean notificationsEnabled;
    private IFile markupFile;

    public MarkdownEditorTracker(PageEditorTracker pageEditorTracker, MarkdownListener markdownListener, FileNature fileNature) {
        instances++;
        instance = instances;
        this.markdownListener = markdownListener;
        this.fileNature = fileNature;
        this.pageEditorTracker = pageEditorTracker;
        notificationsEnabled = true;
        LOGGER.fine("instance: " + instance);
        pageEditorTracker.subscribe(this);
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        LOGGER.fine("notificationsEnabled: " + notificationsEnabled);
        this.notificationsEnabled = notificationsEnabled;
    }

    public void notifyMarkdownListenerAlways() {
        LOGGER.fine("");
        if (markdownListener != null) {
            try {
                markdownListener.showIFile(markupFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        LOGGER.fine("");
        pageEditorTracker.unsubscribe(this);
        pageEditorTracker = null;
        markdownListener = null;
    }

    @Override
    public void editorShown(final IEditorPart editorPart) {
        LOGGER.fine("instance: " + instance);
        IEditorInput editorInput = editorPart.getEditorInput();
        final IFile editorFile = ResourceUtil.getFile(editorInput);
        if (fileNature.isTrackableFile(editorFile) && isNewFile(editorFile)) {
            LOGGER.fine("opening markdown editor found; instance: " + instance);
            markupFile = editorFile;
            notifyMarkdownListenerIfEnabled();
            editorPart.addPropertyListener(new IPropertyListener() {

                @Override
                public void propertyChanged(Object source, int propId) {
                    LOGGER.fine(String.format("%s => %d", source, propId));
                    if (propId == IEditorPart.PROP_DIRTY && !editorPart.isDirty()) {
                        notifyMarkdownListenerIfEnabled();
                    }
                }
            });
        }
    }

    @Override
    public void editorClosed(final IEditorPart editorPart) {
        LOGGER.fine("");
        IEditorInput editorInput = editorPart.getEditorInput();
        final IFile editorFile = ResourceUtil.getFile(editorInput);
        if (fileNature.isTrackableFile(editorFile) && isSameFile(editorFile)) {
            LOGGER.fine("closing markdown editor found");
            markupFile = null;
            notifyMarkdownListenerIfEnabled();
        }
    }

    private boolean isNewFile(IFile editorFile) {
        return markupFile == null || !markupFile.getFullPath().equals(editorFile.getFullPath());
    }

    private boolean isSameFile(IFile editorFile) {
        return markupFile != null && markupFile.getFullPath().equals(editorFile.getFullPath());
    }

    private void notifyMarkdownListenerIfEnabled() {
        LOGGER.fine("");
        if (notificationsEnabled) {
            notifyMarkdownListenerAlways();
        }
    }

}
