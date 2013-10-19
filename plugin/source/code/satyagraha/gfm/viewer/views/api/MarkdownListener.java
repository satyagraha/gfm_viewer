package code.satyagraha.gfm.viewer.views.api;

import java.io.IOException;

import org.eclipse.core.resources.IFile;

public interface MarkdownListener {

    void showIFile(IFile editorFile) throws IOException;

}
