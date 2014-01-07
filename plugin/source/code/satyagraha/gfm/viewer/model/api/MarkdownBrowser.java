package code.satyagraha.gfm.viewer.model.api;

import java.io.File;

public interface MarkdownBrowser {

    void showHtmlFile(File htFileNew);

    File getHtFile();

    void forward();

    void back();

    void dispose();

}