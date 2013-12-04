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

class InjectorImpl implements Injector {

    private final ComponentMonitor componentMonitor;
    private MutablePicoContainer container;
    
    InjectorImpl(Collection<Class<?>> components) {
        componentMonitor = new NullComponentMonitor();
        container = new PicoBuilder().withMonitor(componentMonitor).withCaching().build();
        addComponents(components);
    }
    
    InjectorImpl(InjectorImpl parent, Collection<Class<?>> components) {
        componentMonitor = parent.componentMonitor;
        container = parent.container.makeChildContainer();
        addComponents(components);
    }
    
    private void addComponents(Collection<Class<?>> components) {
        for (Class<?> component : components) {
            container.addComponent(component);
        }
    }
    
    @Override
    public <T> T getInstance(Class<T> componentType) {
        checkContainer();
        if (componentType == null) {
            throw new IllegalArgumentException(new NullPointerException());
        }
        T result = container.getComponent(componentType);
        if (result == null) {
            throw new IllegalArgumentException(new ClassNotFoundException(componentType.getCanonicalName()));
        }
        return result;
    }

    @Override
    public void addInstance(Object object) {
        checkContainer();
        container.addComponent(object);
    }
    
    /**
     * Assign JSR-330 javax.inject.Inject annotated fields for object which is not container-managed
     * 
     * @param instance
     */
    @Override
    public void inject(Object instance) {
        checkContainer();
        Object key = instance.getClass().getCanonicalName();
        Class<?> impl = instance.getClass();
        Parameter[] parameters = null;
        Class<? extends Annotation> injectionAnnotation = Inject.class;
        boolean useNames = false;
        AnnotatedFieldInjector<Object> annotatedFieldInjector = new AnnotatedFieldInjector<Object>(key, impl, parameters, componentMonitor, injectionAnnotation, useNames);
        annotatedFieldInjector.decorateComponentInstance(container, null, instance);
    }
    
    @Override
    public void close() {
        container = null;
    }
    
    private void checkContainer() {
        if (container == null) {
            throw new IllegalStateException("injector is closed");
        }
    }
}
