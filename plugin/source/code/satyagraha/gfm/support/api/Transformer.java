package code.satyagraha.gfm.support.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Transformer {

    List<String> markdownExtensions();
    
    boolean isMarkdownFile(File file);
    
    String transformMarkdownText(String mdText);

    void transformMarkdownFile(File mdFile, File htFile) throws IOException;
    
    String htFilename(String mdFilename);

    File createHtmlFile(File mdFile);

    boolean canSkipTransformation(File mdFile, File htFile);

}
