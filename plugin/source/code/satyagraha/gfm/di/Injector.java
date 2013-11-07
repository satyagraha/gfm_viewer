package code.satyagraha.gfm.di;

public interface Injector {

    public <T> T getInstance(Class<T> componentType);

    public void addInstance(Object object);

    /**
     * Assign JSR-330 javax.inject.Inject annotated fields for object which is not container-managed
     * 
     * @param instance
     */
    public void inject(Object instance);

}