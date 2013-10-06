package code.satyagraha.gfm.viewer.plugin;

import static ch.lambdaj.collection.LambdaCollections.with;
import static code.satyagraha.gfm.di.ComponentMatcher.isComponent;
import static code.satyagraha.gfm.di.DIUtils.getBundleClasses;

import java.util.Collection;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import code.satyagraha.gfm.di.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // Top-level package
    private static final String PACKAGE_PREFIX = "code.satyagraha.gfm";

    // The plug-in ID
    public static final String PLUGIN_ID = "code.satyagraha.gfm.viewer"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    // Instance variables
    private Injector injector;

    // Logging utility class
    private static class LogFormatter extends Formatter {
        
        @Override
        public String format(LogRecord record) {
            return new Date(record.getMillis()) + " " + record.getSourceClassName() + " " + record.getSourceMethodName() + " : " + formatMessage(record) + "\n";
        }
    }
    
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
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        debug("");

        Collection<Class<?>> components = with(getBundleClasses(getBundle(), PACKAGE_PREFIX)).retain(isComponent);
        debug("components: " + components);
        injector = new Injector(components);

        Logger logger = Logger.getLogger(PACKAGE_PREFIX);
        Level level = isDebugging() ? Level.FINE : Level.WARNING;
        logger.setLevel(level);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        handler.setLevel(level);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);

        logger.info("registering logger");
        injector.addInstance(logger);
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
        debug("");
        // gfmConfigRegistration.unregister();
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the associated injector
     * 
     * @return the injector
     */
    public Injector getInjector() {
        return injector;
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

    /**
     * Emit debugging information.
     * 
     * @param message
     */
    public static void debug(String message) {
        if (plugin != null && plugin.isDebugging()) {
            StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
            Date now = new Date();
            String formatted = String.format("%s %s %d %s : %s", now, caller.getFileName(), caller.getLineNumber(), caller.getMethodName(), message);
            System.out.println(formatted);
        }
    }

}
