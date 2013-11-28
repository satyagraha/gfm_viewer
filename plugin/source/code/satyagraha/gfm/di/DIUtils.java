package code.satyagraha.gfm.di;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getFullPath;
import static org.osgi.framework.wiring.BundleWiring.LISTRESOURCES_RECURSE;

import java.util.Collection;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import code.satyagraha.gfm.di.Component.Scope;
import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.group.GroupCondition;

public class DIUtils {

    public static Collection<Class<?>> getBundleClasses(final Bundle bundle, String packagePrefix) {
        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        if (bundleWiring == null) {
            throw new IllegalArgumentException("cannot adapt to BundleWiring: " + bundle);
        }
        String resourcePrefix = "/" + packagePrefix.replace('.', '/') + "/";
        Collection<String> resources = bundleWiring.listResources(resourcePrefix, "*.class", LISTRESOURCES_RECURSE);
        return with(resources).convert(new Converter<String, Class<?>>() {

            @Override
            public Class<?> convert(String resource) {
                try {
                    String className = getFullPath(resource).replace('/', '.') + getBaseName(resource);
                    return bundle.loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    static class ComponentMatcher extends BaseMatcher<Class<?>> {

        @Override
        public boolean matches(Object object) {
            return object != null && object.getClass() == Class.class && ((Class<?>) object).isAnnotationPresent(Component.class);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("not annotated as Component");
        }

        public final static ComponentMatcher isComponent = new ComponentMatcher();

    }

    static class ScopeGroupCondition extends GroupCondition<Class<?>> {

        @Override
        protected String getAdditionalPropertyValue(String property, Object object) {
            return null;
        }

        @Override
        protected String getGroupName() {
            return "scope";
        }

        @Override
        protected Object getGroupValue(Object object) {
            Class<?> component = (Class<?>) object;
            Component annotation = component.getAnnotation(Component.class);
            Scope scope = annotation.value();
            return scope;
        }

    }

}
