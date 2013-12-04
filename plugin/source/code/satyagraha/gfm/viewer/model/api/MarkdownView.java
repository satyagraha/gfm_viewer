package code.satyagraha.gfm.viewer.model.api;

import java.io.File;

public interface MarkdownView {

    public static final String ID = "code.satyagraha.gfm.viewer.views.GfmView";

    void nowShowing(File mdFile, boolean upToDate);
    
}
