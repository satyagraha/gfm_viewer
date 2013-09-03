package code.satyagraha.gfm.support.api;

import java.io.File;
import java.io.IOException;

public interface GfmTransformer {

    public boolean isMarkdownFile(File file);
    
    public String transformMarkdownText(String mdText);

    public void transformMarkdownFile(File mdFile, File htFile) throws IOException;
    
    public String htFilename(String mdFilename);

}