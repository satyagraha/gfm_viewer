package code.satyagraha.gfm.viewer.model.api;

import java.io.IOException;

import org.eclipse.core.resources.IFile;

public interface MarkdownListener {

    void notifyEditorFile(IFile editorFile) throws IOException;

}
