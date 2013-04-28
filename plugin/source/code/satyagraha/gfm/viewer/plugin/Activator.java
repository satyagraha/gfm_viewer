package code.satyagraha.gfm.viewer.plugin;

import java.util.Date;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
   
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
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		debug("");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
	    debug("");
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
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
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
            String formatted = String.format("%s %s : %d %s %s", now, caller.getFileName(), caller.getLineNumber(), caller.getMethodName(), message);
            System.out.println(formatted);
        }
    }

}
