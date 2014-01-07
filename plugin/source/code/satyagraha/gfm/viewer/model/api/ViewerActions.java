package code.satyagraha.gfm.viewer.model.api;

import java.io.IOException;

import org.eclipse.core.resources.IFile;

public interface ViewerActions {

    void showMarkdownFile(IFile editorFile) throws IOException;

    void goForward();

    void goBackward();

    void reload();

}
