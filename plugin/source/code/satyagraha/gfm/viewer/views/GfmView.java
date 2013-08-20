package code.satyagraha.gfm.viewer.views;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import code.satyagraha.gfm.commands.Linked;
import code.satyagraha.gfm.support.api.FileNature;
import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.support.api.GfmTransformer;
import code.satyagraha.gfm.support.api.GfmWebServiceClient;
import code.satyagraha.gfm.support.impl.GfmTransformerDefault;
import code.satyagraha.gfm.support.impl.GfmWebServiceClientDefault;
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

        gfmConfig = new PreferenceAdapter();
        Logger logger = Logger.getLogger(GfmView.class.getCanonicalName());
        logger.setLevel(Level.WARNING);
        GfmWebServiceClient webServiceClient = new GfmWebServiceClientDefault(gfmConfig, logger);
        gfmTransformer = new GfmTransformerDefault(gfmConfig, logger, webServiceClient);

        FileNature markdownFileNature = new FileNature() {
            
            @Override
            public boolean isTrackableFile(IFile iFile) {
                return iFile != null && gfmTransformer.isMarkdownFile(iFile.getLocation().toFile());
            }
        };
        
        editorTracker = new EditorTracker(getSite().getWorkbenchWindow(), this, markdownFileNature);
        editorTracker.setNotificationsEnabled(Linked.isLinked());
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
        return new File(htDir, gfmTransformer.htFilename(mdFile.getName()));
    }

    private void scheduleTransformation(final File mdFile, final File htFile, final Runnable onDone) {
        final String jobName = "Transforming: " + mdFile.getName();
        Job job = new Job(jobName) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IStatus status = Status.OK_STATUS;
                try {
                    gfmTransformer.transformMarkdownFile(mdFile, htFile);
                } catch (IOException e) {
                    status = new Status(Status.ERROR, Activator.PLUGIN_ID, jobName, e);
                }
                return status;
            }
        };
        job.setUser(false);
        job.setSystem(false);
        job.setPriority(Job.SHORT);
        job.addJobChangeListener(new JobChangeAdapter() {
            
            @Override
            public void done(IJobChangeEvent event) {
                if (event.getResult().isOK()) {
                    Display.getDefault().asyncExec(onDone);
                } else {
                    // normal reporting has occurred
                }
            }
        });
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

    public void setLinkedState(boolean state) {
        Activator.debug("state: " + state);
        if (editorTracker != null) {
            editorTracker.setNotificationsEnabled(state);
        }
    }

    public void reload() {
        Activator.debug("");
        if (editorTracker != null) {
             editorTracker.notifyGfmListenerAlways();
        }
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