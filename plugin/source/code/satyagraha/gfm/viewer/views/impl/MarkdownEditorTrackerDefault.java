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
            notifyMarkdownListener(editorFile);
        }

        void close() {
            // notifyMarkdownListenerIfEnabled(null);
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
        this.markdownListener = null;
        this.markdownFileNature = markdownFileNature;
        this.pageEditorTracker = pageEditorTracker;
        subscriptions = new MarkdownEditorSubscriptions();
        currentSubscription = null;
        pageEditorTracker.subscribe(this);
    }

    /* (non-Javadoc)
     * @see code.satyagraha.gfm.viewer.views.impl.MarkdownEditorTracker#addListener(code.satyagraha.gfm.viewer.views.api.MarkdownListener)
     */
    @Override
    public void addListener(MarkdownListener markdownListener) {
        this.markdownListener = markdownListener;
    }
    
    private void notifyMarkdownListener(IFile markdownFile) {
        LOGGER.fine("markdownFile: " + markdownFile);
        if (markdownListener != null) {
            try {
                markdownListener.notifyEditorFile(markdownFile);
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
        
        markdownListener = null;
        pageEditorTracker = null;
    }

    @Override
    public void editorShown(final IEditorPart editorPart) {
        LOGGER.fine("editorPart: " + editorPart);
        IEditorInput editorInput = editorPart.getEditorInput();
        if (editorInput == null) {
            return;
        }
        IFile editorFile = ResourceUtil.getFile(editorInput);
        if (editorFile == null) {
            return;
        }
        if (markdownFileNature.isTrackableFile(editorFile)) {
            LOGGER.fine("editorFile: " + editorFile);
            MarkdownEditorSubscription subscription = subscriptions.get(editorFile);
            if (subscription == null) {
                subscription = new MarkdownEditorSubscription(editorPart, editorFile);
                subscriptions.add(subscription);
                subscription.open();
            } else if (currentSubscription != subscription) {
                notifyMarkdownListener(editorFile);
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

}
