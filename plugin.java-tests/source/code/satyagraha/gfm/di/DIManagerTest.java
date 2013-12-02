package code.satyagraha.gfm.di;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.osgi.framework.wiring.BundleWiring.LISTRESOURCES_RECURSE;

import java.util.Arrays;
import java.util.List;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import ch.lambdaj.collection.LambdaList;
import ch.lambdaj.function.convert.Converter;
import code.satyagraha.gfm.di.Component.Scope;

@RunWith(MockitoJUnitRunner.class)
public class DIManagerTest {

    @Mock
    IWorkbench workbench;

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    BundleWiring bundleWiring;

    String packagePrefix = "code.satyagraha.gfm";
    
    boolean debugging = true;

    @Mock
    IWorkbenchWindow workbenchWindow;

    @Mock
    IWorkbenchPage workbenchPage;
    
    IWorkbenchWindow[] workbenchWindows;
    
    IWorkbenchPage[] workbenchPages;

    public static class NonInjectedClass {
        
    }
    
    @Component(value = Scope.PLUGIN)
    public static class InjectedPluginClass {
        
        private final BundleContext bundleContext;
        
        public InjectedPluginClass(BundleContext bundleContext) {
            this.bundleContext = bundleContext;
        }
        
        public BundleContext getBundleContext() {
            return bundleContext;
        }
        
    }
    
    @Component(value = Scope.PAGE)
    public static class InjectedPageClass {
        
        private final IWorkbenchPage page;

        public InjectedPageClass(IWorkbenchPage page) {
            this.page = page;
        }
        
        public IWorkbenchPage getPage() {
            return page;
        }
        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() throws Exception {
        workbenchWindows = new IWorkbenchWindow[] { workbenchWindow };
        
        workbenchPages = new IWorkbenchPage[] { workbenchPage };
        
        given(workbench.getWorkbenchWindows()).willReturn(workbenchWindows);
        given(bundleContext.getBundle()).willReturn(bundle);
        given(bundle.adapt(BundleWiring.class)).willReturn(bundleWiring);

        List<Class<? extends Object>> bundleClasses = Arrays.asList(NonInjectedClass.class, InjectedPluginClass.class, InjectedPageClass.class);
        LambdaList<String> bundleClassesResourcePaths = with(bundleClasses).convert(new Converter<Class<? extends Object>, String>() {

            @Override
            public String convert(Class<? extends Object> classz) {
                String name = classz.getName();
                return name.replace('.', '/') + ".class" ;
            }
        });
        
        System.out.println("bundleClassesResourcePaths: " + bundleClassesResourcePaths);
        String resourcePrefix = "/" + packagePrefix.replace('.', '/') + "/"; 
        given(bundleWiring.listResources(resourcePrefix, "*.class", LISTRESOURCES_RECURSE)).willReturn(bundleClassesResourcePaths);
        
        given(workbench.getActiveWorkbenchWindow()).willReturn(workbenchWindow);
        
        given(workbenchWindow.getPages()).willReturn(workbenchPages);
        
        given(workbenchWindow.getActivePage()).willReturn(workbenchPage);
        
        Class<?> injectedPluginClass = (Class<?>) InjectedPluginClass.class;
        given(bundle.loadClass(injectedPluginClass.getName())).willReturn((Class) injectedPluginClass);

        Class<?> injectedPageClass = (Class<?>) InjectedPageClass.class;
        given(bundle.loadClass(injectedPageClass.getName())).willReturn((Class) injectedPageClass);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInstantiateNonComponentClass() throws Exception {
        // given
        

        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        @SuppressWarnings("unused")
        NonInjectedClass nonInjectedClassInstance = DIManager.getDefault().getInjector(Scope.PLUGIN).getInstance(NonInjectedClass.class);

        // then
    }


    @Test
    public void shouldInstantiatePluginScopeComponent() throws Exception {
        // given
        

        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        InjectedPluginClass injectedPluginClassInstance = DIManager.getDefault().getInjector(Scope.PLUGIN).getInstance(InjectedPluginClass.class);

        // then
        assertThat(injectedPluginClassInstance, instanceOf(InjectedPluginClass.class));
        assertThat(injectedPluginClassInstance.getBundleContext(), sameInstance(bundleContext));
    }

    @Test
    public void shouldInstantiatePageScopeComponent() throws Exception {
        // given
        
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        InjectedPageClass injectedPageClassInstance = DIManager.getDefault().getInjector(Scope.PAGE).getInstance(InjectedPageClass.class);
        
        // then
        assertThat(injectedPageClassInstance, instanceOf(InjectedPageClass.class));
        assertThat(injectedPageClassInstance.getPage(), sameInstance(workbenchPage));
    }
    
}
