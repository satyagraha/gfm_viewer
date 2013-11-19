package code.satyagraha.gfm.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import code.satyagraha.gfm.di.Component;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.support.api.Config;

@Component(Scope.PLUGIN)
public class LogManager {

    // Console name
    private static final String GFM_CONSOLE = "GFM Console";

    private final Config config;
    private Logger logger;
    private Formatter formatter;
    private Handler consoleHandler;
    private Handler logConsoleHandler;

    private static LogManager logManager;

    public LogManager(Config config) {
        this.config = config;
        AnnotationProcessor.process(this);
    }

    private void setup(String packagePrefix) {
        logger = Logger.getLogger(packagePrefix);
        formatter = new LogFormatter();
        logger.setUseParentHandlers(false);
        configChanged(null);
    }

    @EventSubscriber(eventClass = Config.Changed.class)
    public void configChanged(Config.Changed configChanged) {
        Level level = config.useEclipseConsole() ? Level.FINE : Level.INFO;
        logger.setLevel(level);

        if (consoleHandler == null) {
            consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);
        }

        if (logConsoleHandler == null && config.useEclipseConsole()) {
            LogConsole.start(GFM_CONSOLE);
            logConsoleHandler = LogConsole.getInstance().createHandler(formatter);
            logger.addHandler(logConsoleHandler);
        }

        for (Handler handler : logger.getHandlers()) {
            handler.setLevel(level);
        }
    }

    private void close() {
        if (logConsoleHandler != null) {
            LogConsole.stop();
            logger.removeHandler(logConsoleHandler);
            logConsoleHandler = null;
        }
    }

    public static void start(String packagePrefix) {
        logManager = DIManager.getDefault().getInjector(Scope.PLUGIN).getInstance(LogManager.class);
        logManager.setup(packagePrefix);
    }

    public static void stop() {
        if (logManager != null) {
            logManager.close();
        }
    }

}
