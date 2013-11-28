package code.satyagraha.gfm.viewer.views.api;


public interface MarkdownEditorTracker {

    public void addListener(MarkdownListener markdownListener);

    public void setNotificationsEnabled(boolean notificationsEnabled);

    public void close();

}
