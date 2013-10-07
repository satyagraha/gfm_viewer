package code.satyagraha.gfm.viewer.views;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import code.satyagraha.gfm.support.api.FileNature;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.views.api.Scheduler;
import code.satyagraha.gfm.viewer.views.api.Scheduler.Callback;
import code.satyagraha.gfm.viewer.views.api.ViewSupport;

public class MarkdownView extends ViewPart implements MarkdownListener {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "code.satyagraha.gfm.viewer.views.GfmView";

    private MarkdownBrowser browser;

    private Transformer transformer;

    private Scheduler scheduler;
    
    private ViewSupport viewSupport;
    
    private EditorTracker editorTracker;

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

        transformer = Activator.getDefault().getInjector().getInstance(Transformer.class);
        
        scheduler = Activator.getDefault().getInjector().getInstance(Scheduler.class);
        
        viewSupport = Activator.getDefault().getInjector().getInstance(ViewSupport.class);

        FileNature markdownFileNature = new FileNature() {

            @Override
            public boolean isTrackableFile(IFile iFile) {
                return iFile != null && transformer.isMarkdownFile(iFile.getLocation().toFile());
            }
        };

        editorTracker = new EditorTracker(getSite().getWorkbenchWindow(), this, markdownFileNature);
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
            Activator.debug(iFile.getFullPath().toString());
            showFile(iFile.getRawLocation().toFile());
        } else {
            Activator.debug("(null)");
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
    
    public void goForward() {
        Activator.debug("");
        if (browser != null) {
            browser.forward();
        }
    }

    public void goBackward() {
        Activator.debug("");
        if (browser != null) {
            browser.back();
        }
    }

    public void setLinkedState(boolean state) {
        Activator.debug("state: " + state);
        if (editorTracker != null) {
            editorTracker.setNotificationsEnabled(state);
        }
    }

    public void reload() {
        Activator.debug("");
        if (editorTracker != null) {
            editorTracker.notifyMarkdownListenerAlways();
        }
    }

    public static MarkdownView getInstance() {
        return (MarkdownView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ID);
    }
    
}