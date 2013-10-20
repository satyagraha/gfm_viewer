package code.satyagraha.gfm.di;

import static ch.lambdaj.collection.LambdaCollections.with;
import static code.satyagraha.gfm.di.ComponentMatcher.isComponent;
import static code.satyagraha.gfm.di.DIUtils.getBundleClasses;

import java.util.Collection;

import org.osgi.framework.BundleContext;

public class DIManager {

    private Injector injector;

    private static DIManager instance;
    
    private DIManager(BundleContext bundleContext, String packagePrefix) {
        Collection<Class<?>> components = with(getBundleClasses(bundleContext.getBundle(), packagePrefix)).retain(isComponent);
        injector = new Injector(components);
    }

    public Injector getInjector() {
        return injector;
    }

    public static void start(BundleContext bundleContext, String packagePrefix) {
        instance = new DIManager(bundleContext, packagePrefix);
    }

    public static void stop() {
        instance = null;
    }

    public static DIManager getDefault() {
        return instance;
    }
}
