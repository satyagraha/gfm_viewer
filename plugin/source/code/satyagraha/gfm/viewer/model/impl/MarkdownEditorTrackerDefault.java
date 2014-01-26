package code.satyagraha.gfm.viewer.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ide.ResourceUtil;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.support.api.MarkdownFileNature;
import code.satyagraha.gfm.ui.api.EditorPartListener;
import code.satyagraha.gfm.ui.api.PageEditorTracker;
import code.satyagraha.gfm.viewer.model.api.MarkdownEditorTracker;
import code.satyagraha.gfm.viewer.model.api.MarkdownListener;

@Component(Scope.PAGE)
public class MarkdownEditorTrackerDefault implements MarkdownEditorTracker, EditorPartListener {

    private class MarkdownEditorSubscription implements IPropertyListener {

        private final IEditorPart editorPart;
        private final IFile editorFile;

        MarkdownEditorSubscription(IEditorPart editorPart, IFile editorFile) {
            this.editorPart = editorPart;
            this.editorFile = editorFile;
        }

        void open() {
            editorPart.addPropertyListener(this);
        }

        void close() {
            editorPart.removePropertyListener(this);
        }

        @Override
        public void propertyChanged(Object source, int propId) {
            LOGGER.fine(String.format("%s => %d", source, propId));
            if (propId == IEditorPart.PROP_DIRTY && !editorPart.isDirty()) {
                notifyMarkdownListener(editorFile);
            }
        }
    }

    private class MarkdownEditorSubscriptions {

        private final Map<IFile, MarkdownEditorSubscription> subscriptions;

        MarkdownEditorSubscriptions() {
            subscriptions = new HashMap<IFile, MarkdownEditorSubscription>();
        }
        
        void open() {
            // no-op
        }

        void add(MarkdownEditorSubscription subscription) {
            subscriptions.put(subscription.editorFile, subscription);
        }

        void remove(MarkdownEditorSubscription subscription) {
            subscriptions.remove(subscription.editorFile);
        }

        MarkdownEditorSubscription get(IFile iFile) {
            return subscriptions.get(iFile);
        }

        void close() {
            for (MarkdownEditorSubscription subscription : subscriptions.values()) {
                subscription.close();
            }
            subscriptions.clear();
        }
    }

    private static Logger LOGGER = Logger.getLogger(MarkdownEditorTrackerDefault.class.getPackage().getName());

    private MarkdownListener markdownListener;
    private MarkdownFileNature markdownFileNature;
    private PageEditorTracker pageEditorTracker;
    private MarkdownEditorSubscriptions subscriptions;
    private MarkdownEditorSubscription currentSubscription;

    public MarkdownEditorTrackerDefault(PageEditorTracker pageEditorTracker, MarkdownFileNature markdownFileNature) {
        LOGGER.fine("");
        this.markdownListener = null;
        this.markdownFileNature = markdownFileNature;
        this.pageEditorTracker = pageEditorTracker;
        subscriptions = new MarkdownEditorSubscriptions();
        currentSubscription = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see code.satyagraha.gfm.viewer.model.api.MarkdownEditorTracker#start()
     */
    @Override
    public void start() {
        LOGGER.fine("");
        subscriptions.open();
        pageEditorTracker.subscribe(this);
        currentSubscription = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see code.satyagraha.gfm.viewer.views.impl.MarkdownEditorTracker#close()
     */
    @Override
    public void stop() {
        LOGGER.fine("");
        subscriptions.close();
        pageEditorTracker.unsubscribe(this);
        currentSubscription = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * code.satyagraha.gfm.viewer.views.impl.MarkdownEditorTracker#addListener
     * (code.satyagraha.gfm.viewer.views.api.MarkdownListener)
     */
    @Override
    public void addListener(MarkdownListener markdownListener) {
        LOGGER.fine("");
        this.markdownListener = markdownListener;
    }

    @Override
    public IFile getActiveEditorMarkdownFile() {
        LOGGER.fine("");
        IEditorPart activeEditor = pageEditorTracker.getActiveEditor();
        if (activeEditor == null) {
            return null;
        }
        return getTrackableFile(activeEditor);
    }
    
    private void notifyMarkdownListener(IFile markdownFile) {
        LOGGER.fine("markdownFile: " + markdownFile);
        if (markdownListener != null) {
            markdownListener.notifyEditorFile(markdownFile);
        }
    }

    @Override
    public void editorShown(final IEditorPart editorPart) {
        LOGGER.fine("editorPart: " + editorPart);
        IFile editorFile = getTrackableFile(editorPart);
        if (editorFile == null) {
            return;
        }
        LOGGER.fine("editorFile: " + editorFile);
        MarkdownEditorSubscription subscription = subscriptions.get(editorFile);
        if (subscription == null) {
            subscription = new MarkdownEditorSubscription(editorPart, editorFile);
            subscriptions.add(subscription);
            subscription.open();
        }
        if (subscription != currentSubscription) {
            notifyMarkdownListener(editorFile);
            currentSubscription = subscription;
        }
    }

    @Override
    public void editorClosed(final IEditorPart editorPart) {
        LOGGER.fine("editorPart: " + editorPart);
        IFile editorFile = getTrackableFile(editorPart);
        if (editorFile == null) {
            return;
        }
        LOGGER.fine("editorFile: " + editorFile);
        MarkdownEditorSubscription subscription = subscriptions.get(editorFile);
        if (subscription != null) {
            subscription.close();
            subscriptions.remove(subscription);
        }
        currentSubscription = null;
    }

    private IFile getTrackableFile(IEditorPart editorPart) {
        IFile result = null;
        IEditorInput editorInput = editorPart.getEditorInput();
        if (editorInput != null) {
            IFile editorFile = ResourceUtil.getFile(editorInput);
            if (editorFile != null) {
                if (markdownFileNature.isTrackableFile(editorFile)) {
                    result = editorFile;
                }
            }
        }
        return result;
    }

}
