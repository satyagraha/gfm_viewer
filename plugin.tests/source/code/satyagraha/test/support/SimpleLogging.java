package code.satyagraha.test.support;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleLogging {

    public static boolean isDebugging() {
        return System.getProperty("verbose") != null;
    }
    
    public static void setupLogging() {
        if (isDebugging()) {
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                handler.setLevel(Level.FINE);
            }
            // Set root logger level
            rootLogger.setLevel(Level.FINE);
        }
    }

}
