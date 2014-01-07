package code.satyagraha.gfm.viewer.model.api;

public interface ViewerModel extends ViewerActions {

    void start(MarkdownView markdownView, MarkdownBrowser markdownBrowser);
    
    void stop();
    
}
