package code.satyagraha.gfm.viewer.model.api;

import org.eclipse.core.resources.IFile;

public interface MarkdownEditorTracker {

    void start();

    void stop();

    void addListener(MarkdownListener markdownListener);

    IFile getActiveEditorMarkdownFile();

}
