package code.satyagraha.gfm.support.api;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public interface GfmTransformer {

    public void setConfig(GfmConfig gfmConfig, Logger logger);
    
    public boolean isMarkdownFile(File file);
    
    public String transformMarkdownText(String mdText);

    public void transformMarkdownFile(File mdFile, File htFile) throws IOException;

}