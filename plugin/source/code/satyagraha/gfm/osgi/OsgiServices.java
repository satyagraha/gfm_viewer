package code.satyagraha.gfm.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class OsgiServices {

    private static OsgiServices instance;
    
    private final BundleContext bundleContext;

    public OsgiServices(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    @SuppressWarnings("unchecked")
    public <T> ServiceRegistration<T> register(Class<T> serviceClass, T serviceInstance) {
        return (ServiceRegistration<T>) bundleContext.registerService(serviceClass.getCanonicalName(), serviceInstance, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(Class<T> serviceClass) {
        ServiceReference<?> serviceReference = bundleContext.getServiceReference(serviceClass.getCanonicalName());
        return (T) bundleContext.getService(serviceReference);
    }
    
    public void unregister(Class<?> serviceClass) {
        ServiceReference<?> serviceReference = bundleContext.getServiceReference(serviceClass.getCanonicalName());
        bundleContext.ungetService(serviceReference);
    }
    
    public static void start(BundleContext bundleContext) {
        instance = new OsgiServices(bundleContext);
    }
    
    public static OsgiServices getInstance() {
        return instance;
    }

    //    private static BundleContext getBundleContext() {
//        return FrameworkUtil.getBundle(OsgiServiceRegistry.class).getBundleContext();
//    }
    
}
