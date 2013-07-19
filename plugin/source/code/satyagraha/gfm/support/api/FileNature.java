package code.satyagraha.gfm.support.api;

import org.eclipse.core.resources.IFile;

public interface FileNature {

    public abstract boolean isTrackableFile(IFile iFile);

}