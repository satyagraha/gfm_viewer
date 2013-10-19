package code.satyagraha.gfm.ui.api;

import org.eclipse.ui.IPartListener2;


public interface PageEditorTracker extends IPartListener2 {

    public void subscribe(EditorPartListener listener);

    public void unsubscribe(EditorPartListener listener);

}