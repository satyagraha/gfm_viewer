package code.satyagraha.gfm.viewer.views;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import code.satyagraha.gfm.support.impl.GfmTransformerDefault;
import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.preferences.PreferenceAdapter;

public class GfmView extends ViewPart implements ProgressListener, GfmListener {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = GfmView.class.getCanonicalName();

    private Browser browser;

    private File lastFile;
    private Integer lastScroll;

    private GfmTransformerDefault gfmTransformer;

    private EditorTracker editorTracker;

    @Override
    public void createPartControl(Composite parent) {
        Activator.debug("");

        browser = new Browser(parent, SWT.NONE);
        browser.addProgressListener(this);

        gfmTransformer = new GfmTransformerDefault();
        PreferenceAdapter preferenceAdapter = new PreferenceAdapter();
        Logger logger = Logger.getLogger(GfmView.class.getCanonicalName());
        logger.setLevel(Level.WARNING);
        gfmTransformer.setConfig(preferenceAdapter, logger);

        editorTracker = new EditorTracker(getSite().getWorkbenchWindow(), this);
    }

    @Override
    public void setFocus() {
        Activator.debug("");
    }

    @Override
    public void dispose() {
        Activator.debug("");
        editorTracker.close();
        editorTracker = null;
        gfmTransformer = null;
        browser = null;
        super.dispose();
    }

    @Override
    public void completed(ProgressEvent event) {
        Activator.debug("");
        if (lastScroll != null) {
            browser.execute(String.format("setDocumentScrollTop(%d);", lastScroll));
        }
        showBusy(false);
    }

    @Override
    public void changed(ProgressEvent event) {
        // Activator.debug("");
    }

    @Override
    public void showFile(IFile file) throws IOException {
        if (file != null) {
            Activator.debug(file.getFullPath().toString());
            showBusy(true);
            File mdFile = file.getRawLocation().toFile();
            lastScroll = mdFile.equals(lastFile) ? getScrollTop() : null;
            lastFile = mdFile;

            final File htFile = File.createTempFile(this.getClass().getSimpleName(), ".html");

            scheduleTransformation(mdFile, htFile, new Runnable() {
                
                @Override
                public void run() {
                    browser.setUrl(htFile.toURI().toString());
                }
            });
            
        } else {
            Activator.debug("(null)");
            lastFile = null;
        }
    }

    private Integer getScrollTop() {
        Object position;
        try {
            position = browser.evaluate("return getDocumentScrollTop();");
        } catch (SWTException e) {
            Activator.debug(String.format("%s - %s", e.getClass().getCanonicalName(), e.getMessage()));
            return null;
        }
        Activator.debug(String.format("position: %s", position));
        return position != null ? ((Double) position).intValue() : null;
    }

    private void scheduleTransformation(final File mdFile, final File htFile, final Runnable onDone) {
        Job job = new Job("Transforming: " + mdFile.getName()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    gfmTransformer.transformMarkdownFile(mdFile, htFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Display.getDefault().asyncExec(onDone);
                return Status.OK_STATUS;
            }
        };
        job.setUser(false);
        job.setSystem(false);
        job.setPriority(Job.SHORT);
        job.schedule();
    }

    public static GfmView getInstance() {
        return (GfmView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ID);
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

}