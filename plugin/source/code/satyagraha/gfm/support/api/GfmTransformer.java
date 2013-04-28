package code.satyagraha.gfm.support.api;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public interface GfmTransformer {

    public abstract void setConfig(GfmConfig gfmConfig, Logger logger);
    
    public abstract String transformMarkdownText(String mdText);

    public abstract void transformMarkdownFile(File mdFile, File htFile) throws IOException;


}