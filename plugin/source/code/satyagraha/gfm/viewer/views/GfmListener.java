package code.satyagraha.gfm.viewer.views;

import java.io.IOException;

import org.eclipse.core.resources.IFile;

public interface GfmListener {

    void showFile(IFile editorFile) throws IOException;

}
