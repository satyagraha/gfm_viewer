package code.satyagraha.gfm.viewer.views.impl;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.support.api.MarkdownFileNature;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.ui.api.PageEditorTracker;
import code.satyagraha.gfm.ui.api.Scheduler;
import code.satyagraha.gfm.ui.api.Scheduler.Callback;
import code.satyagraha.gfm.ui.impl.PageEditorTrackerDefault;
import code.satyagraha.gfm.viewer.views.api.MarkdownListener;
import code.satyagraha.gfm.viewer.views.api.ViewerActions;
import code.satyagraha.gfm.viewer.views.api.ViewerSupport;

public class MarkdownView extends ViewPart implements MarkdownListener, ViewerActions {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "code.satyagraha.gfm.viewer.views.GfmView";

    private static int instances = 0;
    private static Logger LOGGER = Logger.getLogger(MarkdownView.class.getPackage().getName());

    private final int instance;
    
    @Inject private Transformer transformer;
    @Inject private Scheduler scheduler;
    @Inject private ViewerSupport viewSupport;
    @Inject private MarkdownFileNature markdownFileNature;
    
    private MarkdownBrowser browser;
    private MarkdownEditorTracker editorTracker;

    public MarkdownView() {
        instances++;
        instance = instances;
        LOGGER.fine("instance: " + instance);
        DIManager.getDefault().getInjector().inject(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        LOGGER.fine("");

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
                MarkdownView.this.completed(event);
            }
        };

        PageEditorTracker pageEditorTracker = new PageEditorTrackerDefault(getSite().getPage());
        editorTracker = new MarkdownEditorTracker(pageEditorTracker, this, markdownFileNature);
        editorTracker.setNotificationsEnabled(viewSupport.isLinked());
    }

    @Override
    public void setFocus() {
        LOGGER.fine("");
    }

    @Override
    public void dispose() {
        LOGGER.fine("");
        editorTracker.close();
        // editorTracker = null;
        // transformer = null;
        // browser.dispose();
        // browser = null;
        super.dispose();
    }

    @Override
    public void showIFile(IFile iFile) {
        if (iFile != null) {
            LOGGER.fine("instance: " + instance + " " + iFile.getFullPath().toString());
            showFile(iFile.getRawLocation().toFile());
        } else {
            LOGGER.fine("instance: " + instance + " (null)");
        }
    }

    private void showFile(File mdFile) {
        LOGGER.fine("mdFile: " + mdFile);
        final File htFile = transformer.createHtmlFile(mdFile);
        if (transformer.canSkipTransformation(mdFile, htFile)) {
            browser.showHtmlFile(mdFile, htFile);
        } else {
            scheduleTransformation(mdFile, htFile);
        }
    }

    private void scheduleTransformation(final File mdFile, final File htFile) {
        scheduler.scheduleTransformation(mdFile, htFile, new Callback<File>() {

            @Override
            public void onComplete(File htFile) {
                browser.showHtmlFile(mdFile, htFile);
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
                LOGGER.log(Level.WARNING, "unable to locate file", e);
            }
        }
    }

    @Override
    public void goForward() {
        LOGGER.fine("");
        if (browser != null) {
            browser.forward();
        }
    }

    @Override
    public void goBackward() {
        LOGGER.fine("");
        if (browser != null) {
            browser.back();
        }
    }

    @Override
    public void setLinkedState(boolean state) {
        LOGGER.fine("state: " + state);
        if (editorTracker != null) {
            editorTracker.setNotificationsEnabled(state);
        }
    }

    @Override
    public void reload() {
        LOGGER.fine("");
        File mdFile = browser.getMdFile();
        File htFile = browser.getHtFile();
        if (mdFile != null && mdFile.exists() && htFile != null) {
            scheduleTransformation(mdFile, htFile);
        }
    }

    private void completed(ProgressEvent event) {
        LOGGER.fine("");
        // the following code is a work-around for the problem of disappearing
        // cursor on Windows
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor instanceof ITextEditor) {
            ITextEditor textEditor = (ITextEditor) editor;
            IAction action = textEditor.getAction(ITextEditorActionDefinitionIds.TOGGLE_OVERWRITE);
            if (action != null) {
                action.run();
                action.run();
            }
        }
    }

}
