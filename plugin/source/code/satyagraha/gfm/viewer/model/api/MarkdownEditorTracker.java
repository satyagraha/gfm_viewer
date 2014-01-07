package code.satyagraha.gfm.viewer.model.api;

public interface MarkdownEditorTracker {

    void start();

    void stop();

    void addListener(MarkdownListener markdownListener);

}
