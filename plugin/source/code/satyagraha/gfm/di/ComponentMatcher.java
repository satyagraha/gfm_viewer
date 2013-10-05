package code.satyagraha.gfm.di;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ComponentMatcher extends BaseMatcher<Class<?>> {

    @Override
    public boolean matches(Object object) {
        return object != null && object.getClass() == Class.class && ((Class<?>)object).isAnnotationPresent(Component.class);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("not annotated as Component");
    }

    public final static ComponentMatcher isComponent = new ComponentMatcher();
    
}
