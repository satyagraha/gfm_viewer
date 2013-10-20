package code.satyagraha.gfm.support.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface Transformer {

    public List<String> markdownExtensions();
    
    public boolean isMarkdownFile(File file);
    
    public String transformMarkdownText(String mdText);

    public void transformMarkdownFile(File mdFile, File htFile) throws IOException;
    
    public String htFilename(String mdFilename);

    public File createHtmlFile(File mdFile);
    
}
