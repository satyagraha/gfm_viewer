package code.satyagraha.gfm.di;

import java.util.Collection;

import org.picocontainer.DefaultPicoContainer;

public class Injector {

    private final DefaultPicoContainer container;
    
    public Injector(Collection<Class<?>> components) {
        container = new DefaultPicoContainer();
        for (Class<?> component : components) {
            container.addComponent(component);
        }
    }
    
    public <T> T getComponent(Class<T> componentType) {
        T result = container.getComponent(componentType);
        if (result == null) {
            throw new IllegalArgumentException("no component found of type: " + componentType);
        }
        return result;
    }
    
}
