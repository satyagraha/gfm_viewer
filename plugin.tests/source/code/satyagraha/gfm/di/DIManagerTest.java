package code.satyagraha.gfm.di;

import static ch.lambdaj.collection.LambdaCollections.with;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.osgi.framework.wiring.BundleWiring.LISTRESOURCES_RECURSE;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import ch.lambdaj.collection.LambdaList;
import ch.lambdaj.function.convert.Converter;
import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.test.support.SimpleLogging;

@RunWith(MockitoJUnitRunner.class)
public class DIManagerTest {

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    BundleWiring bundleWiring;

    @Mock
    IWorkbench workbench;
    
    @Captor
    ArgumentCaptor<IWindowListener> windowListenerCaptor;
    
    @Mock
    IWorkbenchWindow workbenchWindow1, workbenchWindow2;

    @Mock
    IWorkbenchPage workbenchPage1, workbenchPage2;
    
    @Captor
    ArgumentCaptor<IPageListener> pageListenerCaptor1, pageListenerCaptor2;
    
    private boolean debugging = SimpleLogging.isDebugging();
    
    private String packagePrefix = "code.satyagraha.gfm";
    
    static class NonInjectedClass {
        
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
    
    static class InjectablePluginClass {
        
        @Inject
        private InjectedPluginClass injectedPluginInstance;
        
    }
    
    static class InjectablePageClass {
        
        @Inject
        private InjectedPageClass injectedPageInstance;
        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() throws Exception {
        IWorkbenchWindow[] workbenchWindows = new IWorkbenchWindow[] { workbenchWindow1, workbenchWindow2 };
        
        given(workbench.getWorkbenchWindows()).willReturn(workbenchWindows);

        willDoNothing().given(workbench).addWindowListener(windowListenerCaptor.capture());
        
        given(workbench.getActiveWorkbenchWindow()).willReturn(workbenchWindow1);
        
        IWorkbenchPage[] workbenchPages1 = new IWorkbenchPage[] { workbenchPage1 };
        given(workbenchWindow1.getPages()).willReturn(workbenchPages1);
        given(workbenchWindow1.getActivePage()).willReturn(workbenchPage1);
        
        IWorkbenchPage[] workbenchPages2 = new IWorkbenchPage[] { workbenchPage2 };
        given(workbenchWindow2.getPages()).willReturn(workbenchPages2);
        given(workbenchWindow2.getActivePage()).willReturn(workbenchPage2);
        
        given(bundleContext.getBundle()).willReturn(bundle);
        given(bundle.adapt(BundleWiring.class)).willReturn(bundleWiring);

        ///////////////////////////////////////////////////////////////////////
        
        List<Class<? extends Object>> bundleClasses = Arrays.asList(NonInjectedClass.class, InjectedPluginClass.class, InjectedPageClass.class);
        LambdaList<String> bundleClassesResourcePaths = with(bundleClasses).convert(new Converter<Class<? extends Object>, String>() {
            
            @Override
            public String convert(Class<? extends Object> classz) {
                String name = classz.getName();
                return name.replace('.', '/') + ".class" ;
            }
        });
        
        if (debugging) {
            System.out.println("bundleClassesResourcePaths: " + bundleClassesResourcePaths);
        }
        String resourcePrefix = "/" + packagePrefix.replace('.', '/') + "/"; 
        given(bundleWiring.listResources(resourcePrefix, "*.class", LISTRESOURCES_RECURSE)).willReturn(bundleClassesResourcePaths);
        
        Class<?> injectedPluginClass = (Class<?>) InjectedPluginClass.class;
        given(bundle.loadClass(injectedPluginClass.getName())).willReturn((Class) injectedPluginClass);

        Class<?> injectedPageClass = (Class<?>) InjectedPageClass.class;
        given(bundle.loadClass(injectedPageClass.getName())).willReturn((Class) injectedPageClass);
    }

    @Test
    public void shouldStartNormally() {
        // given
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);

        // then
        verify(workbench, times(1)).addWindowListener(any(IWindowListener.class));
        IWindowListener windowListener = windowListenerCaptor.getValue();
        assertThat(windowListener, notNullValue());
    }
    
    @Test
    public void shouldStopNormally() {
        // given
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        DIManager.stop();
        
        // then
        IWindowListener windowListener = windowListenerCaptor.getValue();
        assertThat(windowListener, notNullValue());
        verify(workbench, times(1)).removeWindowListener(eq(windowListener));
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
        assertThat(injectedPageClassInstance.getPage(), sameInstance(workbenchPage1));
    }

    @Test
    public void shouldInstantiateNewPluginScopeComponentWhenNewWindow() throws Exception {
        // given
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        InjectedPageClass injectedPageClassInstance1 = DIManager.getDefault().getInjector(Scope.PAGE).getInstance(InjectedPageClass.class);

        // given
        given(workbench.getActiveWorkbenchWindow()).willReturn(workbenchWindow2);
        
        // when
        IWindowListener windowListener = windowListenerCaptor.getValue();
        windowListener.windowOpened(workbenchWindow2);

        InjectedPageClass injectedPageClassInstance2 = DIManager.getDefault().getInjector(Scope.PAGE).getInstance(InjectedPageClass.class);

        // then
        assertThat(injectedPageClassInstance2, not(sameInstance(injectedPageClassInstance1)));
        
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailPageScopeIfNoWorkbenchWindow() throws Exception {
        // given
        given(workbench.getActiveWorkbenchWindow()).willReturn(null);

        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        @SuppressWarnings("unused")
        InjectedPageClass injectedPageClassInstance = DIManager.getDefault().getInjector(Scope.PAGE).getInstance(InjectedPageClass.class);

        // then
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailPageScopeIfNoWorkbenchPage() throws Exception {
        // given
        given(workbenchWindow1.getActivePage()).willReturn(null);
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        @SuppressWarnings("unused")
        InjectedPageClass injectedPageClassInstance = DIManager.getDefault().getInjector(Scope.PAGE).getInstance(InjectedPageClass.class);
        
        // then
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGetInjectorIfNullScope() throws Exception {
        // given
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        @SuppressWarnings("unused")
        InjectedPageClass injectedPageClassInstance = DIManager.getDefault().getInjector(null).getInstance(InjectedPageClass.class);
        
        // then
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGetInjectorIfNullClass() throws Exception {
        // given
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        @SuppressWarnings("unused")
        InjectedPageClass injectedPageClassInstance = DIManager.getDefault().getInjector(Scope.PLUGIN).getInstance(null);
        
        // then
    }
    
    @Test(expected = IllegalStateException.class)
    public void shouldFailGetInjectorIfStopped() throws Exception {
        // given
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        Injector injector = DIManager.getDefault().getInjector(Scope.PLUGIN);
        DIManager.stop();
        
        @SuppressWarnings("unused")
        InjectedPluginClass injectedPluginClassInstance = injector.getInstance(InjectedPluginClass.class);
        
        // then
    }
    
    @Test
    public void shouldInjectPluginScopeComponent() throws Exception {
        // given

        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        InjectablePluginClass injectablePlugin = new InjectablePluginClass();
        DIManager.getDefault().getInjector(Scope.PLUGIN).inject(injectablePlugin);

        // then
        assertThat(injectablePlugin.injectedPluginInstance, instanceOf(InjectedPluginClass.class));
        assertThat(injectablePlugin.injectedPluginInstance.getBundleContext(), sameInstance(bundleContext));
    }


    @Test
    public void shouldInjectPageScopeComponent() throws Exception {
        // given
        
        // when
        DIManager.start(workbench, bundleContext, packagePrefix, debugging);
        InjectablePageClass injectablePage = new InjectablePageClass();
        DIManager.getDefault().getInjector(Scope.PAGE).inject(injectablePage);
        
        // then
        assertThat(injectablePage.injectedPageInstance, instanceOf(InjectedPageClass.class));
        assertThat(injectablePage.injectedPageInstance.getPage(), sameInstance(workbenchPage1));
    }


}
