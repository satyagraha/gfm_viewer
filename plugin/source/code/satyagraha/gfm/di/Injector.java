package code.satyagraha.gfm.di;

import java.util.Collection;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

public class Injector {

    private final MutablePicoContainer container;
    
    public Injector(Collection<Class<?>> components) {
        container = new PicoBuilder().withCaching().build();
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
    
}
