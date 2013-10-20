package code.satyagraha.gfm.ui.api;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

public interface Scheduler {

    interface Callback<T> {
        void onComplete(T t);
    }

    void scheduleTransformation(File mdFile, Callback<File> onDone);

    void generateIFile(IFile iFile);

    void generateIFolder(IFolder iFolder);

}
