package code.satyagraha.gfm.support.api;

import org.eclipse.core.resources.IFile;

public interface MarkdownFileNature {

    public abstract boolean isTrackableFile(IFile iFile);

}
