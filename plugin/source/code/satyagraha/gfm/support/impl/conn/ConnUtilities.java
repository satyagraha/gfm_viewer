package code.satyagraha.gfm.support.impl.conn;

import static java.util.Arrays.asList;

import java.util.List;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.core.impl.provider.entity.StringProvider;

/**
 * Provide useful connection-related services.
 * 
 */
public class ConnUtilities {

    private static final Class<?>[] JERSEY_PROVIDERS = {
            //
            StringProvider.class,
            //
            JacksonJsonProvider.class };

    /**
     * @return Loaded Jersey providers.
     */
    public static List<Class<?>> getJerseyProviders() {
        return asList(JERSEY_PROVIDERS);
    }

}
