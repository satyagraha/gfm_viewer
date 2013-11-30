package code.satyagraha.gfm.viewer.views.impl;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.ui.api.Scheduler;
import code.satyagraha.gfm.ui.api.Scheduler.Callback;
import code.satyagraha.gfm.viewer.views.api.MarkdownEditorTracker;
import code.satyagraha.gfm.viewer.views.api.MarkdownListener;
import code.satyagraha.gfm.viewer.views.api.ViewerActions;
import code.satyagraha.gfm.viewer.views.api.ViewerSupport;

public class MarkdownView extends ViewPart implements MarkdownListener, ViewerActions {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "code.satyagraha.gfm.viewer.views.GfmView";

    private static Logger LOGGER = Logger.getLogger(MarkdownView.class.getPackage().getName());

    @Inject private Transformer transformer;
    @Inject private Scheduler scheduler;
    @Inject private ViewerSupport viewSupport;
    @Inject private MarkdownEditorTracker editorTracker;
    
    private MarkdownBrowser browser;
    private File mdFileNotified;
    private File mdFileShown;

    public MarkdownView() {
        DIManager.getDefault().getInjector(Scope.PAGE).inject(this);
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
        mdFileNotified = null;
        mdFileShown = null;

        editorTracker.addListener(this);
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
    public void notifyEditorFile(IFile iFile) {
        LOGGER.fine("iFile: " + iFile);
        IPath rawLocation = iFile.getRawLocation();
        if (rawLocation == null) {
            return;
        }
        mdFileNotified = rawLocation.toFile();
        if (!viewSupport.isLinked()) {
            return;
        }
        showFile(mdFileNotified);
    }

    @Override
    public void showMarkdownFile(IFile iFile) throws IOException {
        IPath rawLocation = iFile.getRawLocation();
        if (rawLocation == null) {
            return;
        }
        showFile(rawLocation.toFile());
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
    public void reload() {
        LOGGER.fine("");
        File mdFile = mdFileShown != null ? mdFileShown : mdFileNotified;
        if (mdFile != null && mdFile.exists()) {
            File htFile = transformer.createHtmlFile(mdFile);
            scheduleTransformation(mdFile, htFile);
        }
    }

    private void showFile(File mdFile) {
        LOGGER.fine("mdFile: " + mdFile);
        if (mdFile == null) {
            return;
        }
        final File htFile = transformer.createHtmlFile(mdFile);
        if (transformer.canSkipTransformation(mdFile, htFile)) {
            updateBrowser(mdFile, htFile);
        } else {
            scheduleTransformation(mdFile, htFile);
        }
    }

    private void scheduleTransformation(final File mdFile, final File htFile) {
        scheduler.scheduleTransformation(mdFile, htFile, new Callback<File>() {

            @Override
            public void onComplete(File htFile) {
                updateBrowser(mdFile, htFile);
            }
        });
    }

    private void updateBrowser(File mdFile, File htFile) {
        mdFileShown = mdFile;
        browser.showHtmlFile(htFile);
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
