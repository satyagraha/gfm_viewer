package code.satyagraha.gfm.ui.api;

import org.eclipse.ui.IEditorPart;

public interface EditorPartListener {
    public void editorShown(IEditorPart editor);
    public void editorClosed(IEditorPart editor);
}