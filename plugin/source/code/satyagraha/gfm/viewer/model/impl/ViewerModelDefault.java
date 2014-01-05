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
import code.satyagraha.gfm.viewer.model.api.MarkdownView;
import code.satyagraha.gfm.viewer.model.api.ViewerModel;
import code.satyagraha.gfm.viewer.model.api.ViewerSupport;

@Component(Scope.PAGE)
public class ViewerModelDefault implements ViewerModel, MarkdownListener {

    private static Logger LOGGER = Logger.getLogger(ViewerModelDefault.class.getPackage().getName());

    private Transformer transformer;
    private Scheduler scheduler;
    private ViewerSupport viewSupport;
    private MarkdownEditorTracker editorTracker;
    private MarkdownView markdownView;
    private MarkdownBrowser markdownBrowser;

    private File mdFileShown;

    public ViewerModelDefault(Transformer transformer, Scheduler scheduler, ViewerSupport viewSupport, MarkdownEditorTracker editorTracker) {
        this.transformer = transformer;
        this.scheduler = scheduler;
        this.viewSupport = viewSupport;
        this.editorTracker = editorTracker;
        this.markdownView = null;
        this.markdownBrowser = null;
    }

    @Override
    public void start(MarkdownView markdownView, MarkdownBrowser markdownBrowser) {
        this.markdownView = markdownView;
        this.markdownBrowser = markdownBrowser;
        editorTracker.start();
        editorTracker.addListener(this);
    }

    @Override
    public void stop() {
        editorTracker.stop();
        markdownView = null;
        markdownBrowser = null;
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
        markdownBrowser.forward();
    }

    @Override
    public void goBackward() {
        LOGGER.fine("");
        markdownBrowser.back();
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
            updateBrowser(mdFile, htFile, transformer.canSkipTransformation(mdFile, htFile));
        } else {
            // unable to display
        }
    }

    @Override
    public void notifyEditorFile(IFile editorFile) {
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
            updateBrowser(mdFile, htFile, true);
        } else if (viewSupport.isOnline()) {
            scheduleTransformation(mdFile, htFile);
        } else if (htFile.canRead()) {
            updateBrowser(mdFile, htFile, false); // out-of-date
        } else {
            // unable to display
        }
    }

    private void scheduleTransformation(final File mdFile, final File htFile) {
        Assert.isTrue(viewSupport.isOnline());
        scheduler.scheduleTransformation(mdFile, htFile, new Callback<File>() {

            @Override
            public void onComplete(File htFile) {
                updateBrowser(mdFile, htFile, true);
            }
        });
    }

    private void updateBrowser(File mdFile, File htFile, boolean upToDate) {
        LOGGER.fine("mdFile: " + mdFile);
        mdFileShown = mdFile;
        markdownView.nowShowing(mdFile, upToDate);
        markdownBrowser.showHtmlFile(htFile);
    }

}
