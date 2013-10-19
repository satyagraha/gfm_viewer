package code.satyagraha.gfm.viewer.views.impl;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.support.api.FileNature;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.ui.api.PageEditorTracker;
import code.satyagraha.gfm.ui.api.Scheduler;
import code.satyagraha.gfm.ui.api.Scheduler.Callback;
import code.satyagraha.gfm.ui.impl.PageEditorTrackerDefault;
import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.views.api.MarkdownListener;
import code.satyagraha.gfm.viewer.views.api.ViewerSupport;
import code.satyagraha.gfm.viewer.views.api.ViewerActions;

public class MarkdownView extends ViewPart implements MarkdownListener, ViewerActions {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "code.satyagraha.gfm.viewer.views.GfmView";

    private static int instances = 0;

    private final int instance;
    private MarkdownBrowser browser;
    private Transformer transformer;
    private Scheduler scheduler;
    private ViewerSupport viewSupport;
    private MarkdownEditorTracker editorTracker;

    public MarkdownView() {
        instances++;
        instance = instances;
        Activator.debug("instance: " + instance);
    }
    
    @Override
    public void createPartControl(Composite parent) {
        Activator.debug("");

        browser = new MarkdownBrowser(parent) {

            @Override
            public void handleDrop(File file) {
                if (transformer.isMarkdownFile(file)) {
                    showFile(file);
                }
            }

            @Override
            public void completed(ProgressEvent event) {
                super.completed(event);
                showBusy(false);
            }
        };

        transformer = DIManager.getDefault().getInjector().getInstance(Transformer.class);
        
        scheduler = DIManager.getDefault().getInjector().getInstance(Scheduler.class);
        
        viewSupport = DIManager.getDefault().getInjector().getInstance(ViewerSupport.class);

        FileNature markdownFileNature = new FileNature() {

            @Override
            public boolean isTrackableFile(IFile iFile) {
                return iFile != null && transformer.isMarkdownFile(iFile.getLocation().toFile());
            }
        };

        PageEditorTracker pageEditorTracker = new PageEditorTrackerDefault(getSite().getPage());
        editorTracker = new MarkdownEditorTracker(pageEditorTracker, this, markdownFileNature);
        editorTracker.setNotificationsEnabled(viewSupport.isLinked());

    }

    @Override
    public void setFocus() {
        Activator.debug("");
    }

    @Override
    public void dispose() {
        Activator.debug("");
        editorTracker.close();
//        editorTracker = null;
//        transformer = null;
//        browser.dispose();
//        browser = null;
        super.dispose();
    }

    @Override
    public void showIFile(IFile iFile) {
        if (iFile != null) {
            Activator.debug("instance: " + instance + " " + iFile.getFullPath().toString());
            showFile(iFile.getRawLocation().toFile());
        } else {
            Activator.debug("instance: " + instance + " (null)");
        }
    }

    protected void showFile(File mdFile) {
        showBusy(true);
        scheduler.scheduleTransformation(mdFile, new Callback<File>() {

            @Override
            public void onComplete(File htFile) {
                browser.showHtmlFile(htFile);
            }
        });
    }

    @SuppressWarnings("unused")
    private void refreshFile(final File htFile) {
        Path htPath = new Path(htFile.getAbsolutePath());
        IFile htIFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(htPath);
        if (htIFile != null) {
            try {
                htIFile.refreshLocal(IResource.DEPTH_ZERO, null);
            } catch (CoreException e) {
                Activator.debug(e.toString());
            }
        }
    }
    
    @Override
    public void goForward() {
        Activator.debug("");
        if (browser != null) {
            browser.forward();
        }
    }

    @Override
    public void goBackward() {
        Activator.debug("");
        if (browser != null) {
            browser.back();
        }
    }

    @Override
    public void setLinkedState(boolean state) {
        Activator.debug("state: " + state);
        if (editorTracker != null) {
            editorTracker.setNotificationsEnabled(state);
        }
    }

    @Override
    public void reload() {
        Activator.debug("");
        if (editorTracker != null) {
            editorTracker.notifyMarkdownListenerAlways();
        }
    }

}
