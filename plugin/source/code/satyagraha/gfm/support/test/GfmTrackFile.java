package code.satyagraha.gfm.support.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import code.satyagraha.gfm.support.api.GfmConfig;
import code.satyagraha.gfm.support.api.GfmTransformer;
import code.satyagraha.gfm.support.api.GfmWebServiceClient;
import code.satyagraha.gfm.support.impl.GfmConfigDefault;
import code.satyagraha.gfm.support.impl.GfmTransformerDefault;
import code.satyagraha.gfm.support.impl.GfmWebServiceClientDefault;

public class GfmTrackFile {
    
    private static class GfmConfigTrackFile extends GfmConfigDefault {
        
        private String username;
        private String password;

        public GfmConfigTrackFile(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        @Override
        public String getUsername() {
            return username;
        }
        
        @Override
        public String getPassword() {
            return password;
        }
    }
    
    private GfmTransformer gfmTransformer;
    private Logger logger;

    public GfmTrackFile(GfmConfig gfmConfig) throws IOException {
        logger = Logger.getLogger(GfmTrackFile.class.getCanonicalName());
        GfmWebServiceClient webServiceClient = new GfmWebServiceClientDefault(gfmConfig, logger);
        gfmTransformer = new GfmTransformerDefault(gfmConfig, logger, webServiceClient);
    }
    
    private void manage(String mdFilepath) throws IOException {
        File mdFile = new File(mdFilepath);
        File htFile = new File(mdFilepath + ".html");
        if (htFile.exists()) {
            htFile.delete();
        }
        
        while (true) {
            if (!htFile.exists() || htFile.lastModified() < mdFile.lastModified()) {
                logger.info(String.format("Transforming %s", mdFile));
                gfmTransformer.transformMarkdownFile(mdFile, htFile);
            }
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GfmConfigTrackFile gfmConfig = new GfmConfigTrackFile(args[0], args[1]);
        GfmTrackFile instance = new GfmTrackFile(gfmConfig);
        instance.manage(args[2]);
    }
}
