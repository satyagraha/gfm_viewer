package code.satyagraha.gfm.di;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

public class InjectorTest {

    public static class Component1 {

    }

    public static class Component2 {

        @Inject
        public Component1 component1;

    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetComponent() {
        // given
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.addAll(asList(Component1.class));
        Injector injector = new InjectorImpl(components);

        // when
        Component1 instance1 = injector.getInstance(Component1.class);

        // then
        assertThat(instance1, instanceOf(Component1.class));
    }

    @Test
    public void shouldAddComponent() {
        // given
        List<Class<?>> components = new ArrayList<Class<?>>();
        Injector injector = new InjectorImpl(components);

        // when
        Component1 instance1 = new Component1();
        injector.addInstance(instance1);
        Component1 instance1alias = injector.getInstance(Component1.class);

        // then
        assertThat(instance1alias, sameInstance(instance1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRemoveComponent() {
        // given
        List<Class<?>> components = new ArrayList<Class<?>>();
        Injector injector = new InjectorImpl(components);

        // when
        Component1 instance1 = new Component1();
        injector.addInstance(instance1);
        injector.removeInstance(instance1);
        injector.getInstance(Component1.class);

        // then
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void shouldRejectNullComponentRequest() {
        // given
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.addAll(asList(Component1.class));
        Injector injector = new InjectorImpl(components);

        // when
        injector.getInstance(null);

        // then
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldInjectComponent() {
        // given
        List<Class<?>> components = new ArrayList<Class<?>>();
        components.addAll(asList(Component1.class));
        Injector injector = new InjectorImpl(components);
        Component2 component2 = new Component2();

        // when
        injector.inject(component2);

        // then
        assertThat(component2.component1, instanceOf(Component1.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSupportChildInjectors() {
        // given
        List<Class<?>> components1 = new ArrayList<Class<?>>();
        components1.addAll(asList(Component1.class));
        InjectorImpl injector1 = new InjectorImpl(components1);
        List<Class<?>> components2 = new ArrayList<Class<?>>();
        components2.addAll(asList(Component2.class));
        Injector injector2 = new InjectorImpl(injector1, components2);

        // when
        Component1 instance1 = injector2.getInstance(Component1.class);
        Component2 instance2 = injector2.getInstance(Component2.class);

        // then
        assertThat(instance1, instanceOf(Component1.class));
        assertThat(instance2, instanceOf(Component2.class));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRejectGetComponentIfClosed() {
        // given
        List<Class<?>> components = new ArrayList<Class<?>>();
        Injector injector = new InjectorImpl(components);
        injector.close();

        // when
        injector.getInstance(Component1.class);

        // then
    }

}
