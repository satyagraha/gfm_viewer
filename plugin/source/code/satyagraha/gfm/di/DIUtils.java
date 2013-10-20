package code.satyagraha.gfm.di;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getFullPath;
import static org.osgi.framework.wiring.BundleWiring.LISTRESOURCES_RECURSE;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import ch.lambdaj.function.convert.Converter;

public class DIUtils {
    
    public static Collection<Class<?>> getBundleClasses(final Bundle bundle, String packagePrefix) {
        String resourcePrefix = "/" + packagePrefix.replace('.', '/') + "/";
        Collection<String> resources = bundle.adapt(BundleWiring.class).listResources(resourcePrefix, "*.class", LISTRESOURCES_RECURSE);
        return with(resources).convert(new Converter<String, Class<?>>() {

            @Override
            public Class<?> convert(String resource) {
                String className = getFullPath(resource).replace('/', '.') + getBaseName(resource);
                Class<?> resourceClass;
                try {
                    resourceClass = bundle.loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return resourceClass;
            }
        });
    }
    
}
