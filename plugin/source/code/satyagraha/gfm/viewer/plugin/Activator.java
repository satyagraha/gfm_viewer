package code.satyagraha.gfm.viewer.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.eventbus.EventBusManager;
import code.satyagraha.gfm.logging.LogManager;

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

        if (PlatformUI.isWorkbenchRunning()) {
            DIManager.start(PlatformUI.getWorkbench(), bundleContext, PACKAGE_PREFIX, isDebugging());
            EventBusManager.start();
            LogManager.start(PACKAGE_PREFIX);
        }
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
        if (PlatformUI.isWorkbenchRunning()) {
            LogManager.stop();
            EventBusManager.stop();
            DIManager.stop();
        }
        
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
