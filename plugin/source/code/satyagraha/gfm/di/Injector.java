package code.satyagraha.gfm.di;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.inject.Inject;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoBuilder;
import org.picocontainer.injectors.AnnotatedFieldInjector;
import org.picocontainer.monitors.NullComponentMonitor;

public class Injector {

    private final ComponentMonitor componentMonitor;
    private final MutablePicoContainer container;
    
    public Injector(Collection<Class<?>> components) {
        componentMonitor = new NullComponentMonitor();
        container = new PicoBuilder().withMonitor(componentMonitor).withCaching().build();
        addComponents(components);
    }
    
    public Injector(Injector parent, Collection<Class<?>> components) {
        componentMonitor = parent.componentMonitor;
        container = parent.container.makeChildContainer();
        addComponents(components);
    }
    
    private void addComponents(Collection<Class<?>> components) {
        for (Class<?> component : components) {
            container.addComponent(component);
        }
    }
    
    public <T> T getInstance(Class<T> componentType) {
        T result = container.getComponent(componentType);
        if (result == null) {
            throw new IllegalArgumentException("no component found of type: " + componentType);
        }
        return result;
    }

    public void addInstance(Object object) {
        container.addComponent(object);
    }
    
    /**
     * Assign JSR-330 javax.inject.Inject annotated fields for object which is not container-managed
     * 
     * @param instance
     */
    public void inject(Object instance) {
        Object key = instance.getClass().getCanonicalName();
        Class<?> impl = instance.getClass();
        Parameter[] parameters = null;
        Class<? extends Annotation> injectionAnnotation = Inject.class;
        boolean useNames = false;
        AnnotatedFieldInjector<Object> annotatedFieldInjector = new AnnotatedFieldInjector<Object>(key, impl, parameters, componentMonitor, injectionAnnotation, useNames);
        annotatedFieldInjector.decorateComponentInstance(container, null, instance);
    }
    
}
