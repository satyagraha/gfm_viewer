package code.satyagraha.gfm.support.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import code.satyagraha.gfm.support.api.Config;
import code.satyagraha.gfm.support.api.Transformer;
import code.satyagraha.gfm.support.api.WebServiceClient;
import code.satyagraha.gfm.support.impl.ConfigDefault;
import code.satyagraha.gfm.support.impl.TransformerDefault;
import code.satyagraha.gfm.support.impl.WebServiceClientDefault;

public class TrackFile {
    
    private static class ConfigTrackFile extends ConfigDefault {
        
        private String username;
        private String password;

        public ConfigTrackFile(String username, String password) {
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
    
    private Transformer transformer;
    private Logger logger;

    public TrackFile(Config config) throws IOException {
        logger = Logger.getLogger(TrackFile.class.getCanonicalName());
        WebServiceClient webServiceClient = new WebServiceClientDefault(config, logger);
        transformer = new TransformerDefault(config, logger, webServiceClient);
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
                transformer.transformMarkdownFile(mdFile, htFile);
            }
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ConfigTrackFile config = new ConfigTrackFile(args[0], args[1]);
        TrackFile instance = new TrackFile(config);
        instance.manage(args[2]);
    }
}
