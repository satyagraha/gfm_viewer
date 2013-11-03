package code.satyagraha.gfm.viewer.views.impl;

import java.io.IOException;
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
import code.satyagraha.gfm.viewer.views.api.MarkdownEditorTracker;
import code.satyagraha.gfm.viewer.views.api.MarkdownListener;

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
            notifyMarkdownListenerIfEnabled(editorFile);
        }

        void close() {
            // notifyMarkdownListenerIfEnabled(null);
            editorPart.removePropertyListener(this);
        }

        @Override
        public void propertyChanged(Object source, int propId) {
            LOGGER.fine(String.format("%s => %d", source, propId));
            if (propId == IEditorPart.PROP_DIRTY && !editorPart.isDirty()) {
                notifyMarkdownListenerIfEnabled(editorFile);
            }
        }
    }
    
    private class MarkdownEditorSubscriptions {
        
        private final Map<IFile, MarkdownEditorSubscription> subscriptions;
        
        MarkdownEditorSubscriptions() {
            subscriptions = new HashMap<IFile, MarkdownEditorSubscription>();
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

    private static int instances = 0;
    private static Logger LOGGER = Logger.getLogger(MarkdownEditorTrackerDefault.class.getPackage().getName());

    private final int instance;
    private MarkdownListener markdownListener;
    private MarkdownFileNature markdownFileNature;
    private PageEditorTracker pageEditorTracker;
    private MarkdownEditorSubscriptions subscriptions;
    private MarkdownEditorSubscription currentSubscription;
    private boolean notificationsEnabled;

    public MarkdownEditorTrackerDefault(PageEditorTracker pageEditorTracker, MarkdownFileNature markdownFileNature) {
        instances++;
        instance = instances;
        this.markdownListener = null;
        this.markdownFileNature = markdownFileNature;
        this.pageEditorTracker = pageEditorTracker;
        subscriptions = new MarkdownEditorSubscriptions();
        currentSubscription = null;
        notificationsEnabled = true;
        LOGGER.fine("instance: " + instance);
        pageEditorTracker.subscribe(this);
    }

    /* (non-Javadoc)
     * @see code.satyagraha.gfm.viewer.views.impl.MarkdownEditorTracker#addListener(code.satyagraha.gfm.viewer.views.api.MarkdownListener)
     */
    @Override
    public void addListener(MarkdownListener markdownListener) {
        this.markdownListener = markdownListener;
    }
    
    /* (non-Javadoc)
     * @see code.satyagraha.gfm.viewer.views.impl.MarkdownEditorTracker#setNotificationsEnabled(boolean)
     */
    @Override
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        LOGGER.fine("notificationsEnabled: " + notificationsEnabled);
        this.notificationsEnabled = notificationsEnabled;
    }

    public void notifyMarkdownListenerAlways(IFile markdownFile) {
        LOGGER.fine("");
        if (markdownListener != null) {
            try {
                markdownListener.showIFile(markdownFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* (non-Javadoc)
     * @see code.satyagraha.gfm.viewer.views.impl.MarkdownEditorTracker#close()
     */
    @Override
    public void close() {
        LOGGER.fine("");
        subscriptions.close();
        pageEditorTracker.unsubscribe(this);
        currentSubscription = null;
        notificationsEnabled = false;
        
        markdownListener = null;
        pageEditorTracker = null;
    }

    @Override
    public void editorShown(final IEditorPart editorPart) {
        LOGGER.fine("instance: " + instance);
        IEditorInput editorInput = editorPart.getEditorInput();
        IFile editorFile = ResourceUtil.getFile(editorInput);
        if (markdownFileNature.isTrackableFile(editorFile)) {
            LOGGER.fine("opening markdown editor found; instance: " + instance);
            MarkdownEditorSubscription subscription = subscriptions.get(editorFile);
            if (subscription == null) {
                subscription = new MarkdownEditorSubscription(editorPart, editorFile);
                subscriptions.add(subscription);
                subscription.open();
            } else if (currentSubscription != subscription) {
                notifyMarkdownListenerIfEnabled(editorFile);
            }
            currentSubscription = subscription;
        }
    }

    @Override
    public void editorClosed(final IEditorPart editorPart) {
        LOGGER.fine("");
        IEditorInput editorInput = editorPart.getEditorInput();
        IFile editorFile = ResourceUtil.getFile(editorInput);
        if (markdownFileNature.isTrackableFile(editorFile)) {
            LOGGER.fine("closing markdown editor found");
            MarkdownEditorSubscription subscription = subscriptions.get(editorFile);
            if (subscription != null) {
                subscription.close();
                subscriptions.remove(subscription);
            }
            currentSubscription = null;
        }
    }

    private void notifyMarkdownListenerIfEnabled(IFile editorFile) {
        LOGGER.fine("");
        if (notificationsEnabled) {
            notifyMarkdownListenerAlways(editorFile);
        }
    }

}
