package code.satyagraha.gfm.viewer.plugin;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.logging.LogConsole;
import code.satyagraha.gfm.logging.LogFormatter;
import code.satyagraha.gfm.support.api.Config;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // Top-level package
    private static final String PACKAGE_PREFIX = "code.satyagraha.gfm";

    // Console name
    private static final String GFM_CONSOLE = "GFM Console";
    
    // The plug-in ID
    public static final String PLUGIN_ID = "code.satyagraha.gfm.viewer"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        plugin = this;

        DIManager.start(bundleContext, PACKAGE_PREFIX);
        setupLogging();
    }

    private void setupLogging() {
        Config config = DIManager.getDefault().getInjector().getInstance(Config.class);
        
        Logger logger = Logger.getLogger(PACKAGE_PREFIX);
        Level level = isDebugging() || config.useEclipseConsole() ? Level.FINE : Level.INFO;
        logger.setLevel(level);
        
        Formatter formatter = new LogFormatter();
        
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(level);
        logger.addHandler(consoleHandler);
        
        if (config.useEclipseConsole()) {
            LogConsole.start(GFM_CONSOLE);
            Handler logConsoleHandler = LogConsole.getInstance().createHandler(formatter);
            logConsoleHandler.setLevel(level);
            logger.addHandler(logConsoleHandler);
        }
        
        logger.setUseParentHandlers(false);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        LogConsole.stop();
        // gfmConfigRegistration.unregister();
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

}
