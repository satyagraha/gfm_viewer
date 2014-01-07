package code.satyagraha.gfm.viewer.model.api;

import org.eclipse.core.resources.IFile;

public interface MarkdownListener {

    void notifyEditorFile(IFile editorFile);

}
