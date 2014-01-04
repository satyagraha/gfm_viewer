package code.satyagraha.gfm.di;

public interface Injector {

    <T> T getInstance(Class<T> componentType);

    void addInstance(Object object);
    
    void removeInstance(Object object);

    /**
     * Assign JSR-330 javax.inject.Inject annotated fields for object which is not container-managed
     * 
     * @param instance
     */
    void inject(Object instance);
    
    void close();

}
