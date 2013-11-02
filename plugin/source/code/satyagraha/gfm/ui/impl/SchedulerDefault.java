package code.satyagraha.gfm.ui.impl;

import static org.apache.commons.io.FileUtils.iterateFiles;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.ui.api.Scheduler;

@Component
public class SchedulerDefault implements Scheduler {

    private static Logger LOGGER = Logger.getLogger(SchedulerDefault.class.getPackage().getName());

    private final Transformer transformer;
    private final IOFileFilter markdownFileFilter;
    private final String pluginId = "unknown"; // TODO
  
    public SchedulerDefault(Transformer transformer) {
        this.transformer = transformer;
        
        markdownFileFilter = new IOFileFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return accept(new File(dir, name));
            }

            @Override
            public boolean accept(File file) {
                return SchedulerDefault.this.transformer.isMarkdownFile(file);
            }
        };
    }
    
    @Override
    public void scheduleTransformation(final File mdFile, final File htFile, final Callback<File> onDone) {
        final String jobName = "Transforming: " + mdFile.getName();
        Job job = new Job(jobName) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IStatus status = Status.OK_STATUS;
                try {
                    transformer.transformMarkdownFile(mdFile, htFile);
                } catch (IOException e) {
                    status = new Status(Status.ERROR, pluginId, jobName, e);
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
//                in principle, the following line should be enabled, but it appears to force project rebuild
//                refreshFile(htFile);
                if (event.getResult().isOK()) {
                    if (onDone != null) {
                        Display.getDefault().asyncExec(new Runnable() {
                            
                            @Override
                            public void run() {
                                onDone.onComplete(htFile);
                            }
                        });
                    }
                } else {
                    // normal reporting has occurred
                }
            }

        });
        job.schedule();
    }

    @Override
    public void generateIFile(IFile iFile) {
        LOGGER.fine("iFile: " + iFile);
        generateFile(iFile.getRawLocation().toFile());
    }

    @Override
    public void generateIFolder(IFolder iFolder) {
        LOGGER.fine("iFolder: " + iFolder);
        File folder = iFolder.getRawLocation().toFile();
        for (Iterator<File> files = iterateFiles(folder, markdownFileFilter, TrueFileFilter.INSTANCE); files.hasNext();) {
            File file = files.next();
            generateFile(file);
        }
    }

    private void generateFile(File mdFile) {
        LOGGER.fine("mdFile: " + mdFile);
        final File htFile = transformer.createHtmlFile(mdFile);
        scheduleTransformation(mdFile, htFile, null);
    }
    
}
