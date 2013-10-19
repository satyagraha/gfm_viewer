package code.satyagraha.gfm.viewer.views.impl;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ide.ResourceUtil;

import code.satyagraha.gfm.support.api.FileNature;
import code.satyagraha.gfm.ui.api.EditorPartListener;
import code.satyagraha.gfm.ui.api.PageEditorTracker;
import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.views.api.MarkdownListener;

public class MarkdownEditorTracker implements EditorPartListener {

    private static int instances = 0;

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
        Activator.debug("instance: " + instance);
        pageEditorTracker.subscribe(this);
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        Activator.debug("notificationsEnabled: " + notificationsEnabled);
        this.notificationsEnabled = notificationsEnabled;
    }

    public void notifyMarkdownListenerAlways() {
        Activator.debug("");
        if (markdownListener != null) {
            try {
                markdownListener.showIFile(markupFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        Activator.debug("");
        pageEditorTracker.unsubscribe(this);
        pageEditorTracker = null;
        markdownListener = null;
    }

    @Override
    public void editorShown(final IEditorPart editorPart) {
        Activator.debug("instance: " + instance);
        IEditorInput editorInput = editorPart.getEditorInput();
        final IFile editorFile = ResourceUtil.getFile(editorInput);
        if (fileNature.isTrackableFile(editorFile) && isNewFile(editorFile)) {
            Activator.debug("opening markdown editor found; instance: " + instance);
            markupFile = editorFile;
            notifyMarkdownListenerIfEnabled();
            editorPart.addPropertyListener(new IPropertyListener() {

                @Override
                public void propertyChanged(Object source, int propId) {
                    Activator.debug(String.format("%s => %d", source, propId));
                    if (propId == IEditorPart.PROP_DIRTY && !editorPart.isDirty()) {
                        notifyMarkdownListenerIfEnabled();
                    }
                }
            });
        }
    }

    @Override
    public void editorClosed(final IEditorPart editorPart) {
        Activator.debug("");
        IEditorInput editorInput = editorPart.getEditorInput();
        final IFile editorFile = ResourceUtil.getFile(editorInput);
        if (fileNature.isTrackableFile(editorFile) && isSameFile(editorFile)) {
            Activator.debug("closing markdown editor found");
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
        Activator.debug("");
        if (notificationsEnabled) {
            notifyMarkdownListenerAlways();
        }
    }

}
