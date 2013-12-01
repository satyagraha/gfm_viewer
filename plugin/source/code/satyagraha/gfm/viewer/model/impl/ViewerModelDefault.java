package code.satyagraha.gfm.viewer.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.ui.api.Scheduler;
import code.satyagraha.gfm.ui.api.Scheduler.Callback;
import code.satyagraha.gfm.viewer.model.api.MarkdownBrowser;
import code.satyagraha.gfm.viewer.model.api.MarkdownEditorTracker;
import code.satyagraha.gfm.viewer.model.api.MarkdownListener;
import code.satyagraha.gfm.viewer.model.api.ViewerModel;
import code.satyagraha.gfm.viewer.model.api.ViewerSupport;

@Component(Scope.PAGE)
public class ViewerModelDefault implements ViewerModel, MarkdownListener {

    private static Logger LOGGER = Logger.getLogger(ViewerModelDefault.class.getPackage().getName());

    private Transformer transformer;
    private Scheduler scheduler;
    private ViewerSupport viewSupport;
    private MarkdownEditorTracker editorTracker;
    private MarkdownBrowser browser;

    private File mdFileShown;

    public ViewerModelDefault(Transformer transformer, Scheduler scheduler, ViewerSupport viewSupport, MarkdownEditorTracker editorTracker,
            MarkdownBrowser browser) {
        this.transformer = transformer;
        this.scheduler = scheduler;
        this.viewSupport = viewSupport;
        this.editorTracker = editorTracker;
        this.browser = browser;
    }

    @Override
    public void start() {
        editorTracker.start();
        editorTracker.addListener(this);
    }

    @Override
    public void stop() {
        editorTracker.stop();
    }

    @Override
    public void showMarkdownFile(IFile iFile) throws IOException {
        LOGGER.fine("iFile: " + iFile);
        IPath rawLocation = iFile.getRawLocation();
        if (rawLocation == null) {
            return;
        }
        showFile(rawLocation.toFile());
    }

    @Override
    public void goForward() {
        LOGGER.fine("");
        browser.forward();
    }

    @Override
    public void goBackward() {
        LOGGER.fine("");
        browser.back();
    }

    @Override
    public void reload() {
        LOGGER.fine("mdFileShown: " + mdFileShown);
        File mdFile = mdFileShown;
        if (mdFile == null || !mdFile.canRead()) {
            return;
        }
        File htFile = transformer.createHtmlFile(mdFile);
        if (viewSupport.isOnline()) {
            scheduleTransformation(mdFile, htFile);
        } else if (htFile.canRead()) {
            updateBrowser(mdFile, htFile); // may or may not be out-of-date
        } else {
            // unable to display
        }   
    }

    @Override
    public void notifyEditorFile(IFile editorFile) throws IOException {
        LOGGER.fine("editorFile: " + editorFile);
        if (!viewSupport.isLinked()) {
            return;
        }
        IPath rawLocation = editorFile.getRawLocation();
        if (rawLocation == null) {
            return;
        }
        showFile(rawLocation.toFile());
    }

    private void showFile(File mdFile) {
        LOGGER.fine("mdFile: " + mdFile);
        if (mdFile == null || !mdFile.canRead()) {
            return;
        }
        final File htFile = transformer.createHtmlFile(mdFile);
        if (transformer.canSkipTransformation(mdFile, htFile)) {
            updateBrowser(mdFile, htFile);
        } else if (viewSupport.isOnline()) {
            scheduleTransformation(mdFile, htFile);
        } else if (htFile.canRead()) {
            updateBrowser(mdFile, htFile); // out-of-date
        } else {
            // unable to display
        }
    }

    private void scheduleTransformation(final File mdFile, final File htFile) {
        Assert.isTrue(viewSupport.isOnline());
        scheduler.scheduleTransformation(mdFile, htFile, new Callback<File>() {

            @Override
            public void onComplete(File htFile) {
                updateBrowser(mdFile, htFile);
            }
        });
    }

    private void updateBrowser(File mdFile, File htFile) {
        LOGGER.fine("mdFile: " + mdFile);
        mdFileShown = mdFile;
        browser.showHtmlFile(htFile);
    }

}
