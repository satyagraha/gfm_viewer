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
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.support.api.GfmTransformer;
import code.satyagraha.gfm.support.impl.GfmTransformerDefault;
import code.satyagraha.gfm.viewer.plugin.Activator;
import code.satyagraha.gfm.viewer.preferences.PreferenceAdapter;

public class GfmView extends ViewPart implements GfmListener {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = GfmView.class.getCanonicalName();

    private GfmBrowser gfmBrowser;

    private GfmTransformer gfmTransformer;
    
    private GfmConfig gfmConfig;

    private EditorTracker editorTracker;

    @Override
    public void createPartControl(Composite parent) {
        Activator.debug("");

        gfmBrowser = new GfmBrowser(parent) {

            @Override
            public void handleDrop(File file) {
                if (gfmTransformer.isMarkdownFile(file)) {
                    showFile(file);
                }
            }

            @Override
            public void completed(ProgressEvent event) {
                super.completed(event);
                showBusy(false);
            }
        };

        gfmTransformer = new GfmTransformerDefault();
        gfmConfig = new PreferenceAdapter();
        Logger logger = Logger.getLogger(GfmView.class.getCanonicalName());
        logger.setLevel(Level.WARNING);
        gfmTransformer.setConfig(gfmConfig, logger);

        editorTracker = new EditorTracker(getSite().getWorkbenchWindow(), this) {

            @Override
            protected boolean isTrackableFile(IFile iFile) {
                return iFile != null && gfmTransformer.isMarkdownFile(iFile.getLocation().toFile());
            }

        };
        
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
        gfmBrowser.dispose();
        gfmBrowser = null;
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
        final File htFile = createHtmlFile(mdFile);
        scheduleTransformation(mdFile, htFile, new Runnable() {

            @Override
            public void run() {
                gfmBrowser.showHtmlFile(htFile);
            }
        });
    }

    private File createHtmlFile(File mdFile) {
        String htDir = gfmConfig.useTempDir() ? System.getProperty("java.io.tmpdir") : mdFile.getParent();
        String htName = String.format(".%s.html", mdFile.getName());
        return new File(htDir, htName);
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

    public void goForward() {
        Activator.debug("");
        if (gfmBrowser != null) {
            gfmBrowser.forward();
        }
    }

    public void goBackward() {
        Activator.debug("");
        if (gfmBrowser != null) {
            gfmBrowser.back();
        }
    }

    public static GfmView getInstance() {
        return (GfmView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ID);
    }

// (experimental) 
//    private void showToolbarContributions() {
//        IActionBars actionBars = this.getViewSite().getActionBars();
//        if (actionBars != null) {
//            IToolBarManager toolBarManager = actionBars.getToolBarManager();
//            if (toolBarManager != null) {
//                IContributionItem[] contributionItems = toolBarManager.getItems();
//                for (IContributionItem contributionItem : contributionItems) {
//                    Activator.debug("contributionItem: " + contributionItem.toString());
//                    org.eclipse.jface.action.ContributionItem ci;
//                    org.eclipse.ui.IViewSite ivs;
//                    org.eclipse.swt.widgets.ToolItem ti;
//                }
//            }
//        }
//    }

}