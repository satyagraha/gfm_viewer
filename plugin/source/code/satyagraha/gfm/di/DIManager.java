package code.satyagraha.gfm.di;

import static ch.lambdaj.collection.LambdaCollections.with;
import static code.satyagraha.gfm.di.DIUtils.getBundleClasses;
import static code.satyagraha.gfm.di.DIUtils.ComponentMatcher.isComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

import ch.lambdaj.collection.LambdaGroup;
import ch.lambdaj.collection.LambdaList;
import code.satyagraha.gfm.di.Component.Scope;

public class DIManager {

    private static DIManager instance;

    private LambdaGroup<Class<?>> scopeComponentsGroup;
    private Injector pluginInjector;
    private Map<IWorkbenchPage, Injector> pageInjectorMap;
    private WindowListener windowListener;

    private class PageListener implements IPageListener {

        void alreadyOpened(IWorkbenchPage page) {
            debug("PageListener.alreadyOpened");
            pageOpened(page);
        }

        @Override
        public void pageOpened(IWorkbenchPage page) {
            debug("PageListener.pageOpened");
            Injector pageInjector = new Injector(pluginInjector, scopeComponentsGroup.find(Scope.PAGE));
            pageInjector.addInstance(page);
            pageInjectorMap.put(page, pageInjector);
        }

        @Override
        public void pageActivated(IWorkbenchPage page) {
        }

        @Override
        public void pageClosed(IWorkbenchPage page) {
            debug("PageListener.pageClosed");
            pageInjectorMap.remove(page);
        }

    }

    private class WindowListener implements IWindowListener {

        private final Map<IWorkbenchWindow, PageListener> pageListeners;

        WindowListener() {
            pageListeners = new IdentityHashMap<IWorkbenchWindow, PageListener>();
        }

        void alreadyOpened(IWorkbenchWindow window) {
            debug("WindowListener.alreadyOpened");
            windowOpened(window);
            PageListener pageListener = pageListeners.get(window);
            for (IWorkbenchPage page : window.getPages()) {
                pageListener.alreadyOpened(page);
            }
        }

        @Override
        public void windowOpened(IWorkbenchWindow window) {
            debug("WindowListener.windowOpened");
            PageListener pageListener = pageListeners.get(window);
            if (pageListener == null) {
                pageListener = new PageListener();
                pageListeners.put(window, pageListener);
                window.addPageListener(pageListener);
            }
        }

        @Override
        public void windowActivated(IWorkbenchWindow window) {
        }

        @Override
        public void windowDeactivated(IWorkbenchWindow window) {
        }

        @Override
        public void windowClosed(IWorkbenchWindow window) {
            debug("WindowListener.windowClosed");
            PageListener pageListener = pageListeners.get(window);
            if (pageListener != null) {
                pageListeners.remove(window);
                window.removePageListener(pageListener);
            }
        }

    }

    private DIManager(BundleContext bundleContext, String packagePrefix) {
        Collection<Class<?>> components = with(getBundleClasses(bundleContext.getBundle(), packagePrefix)).retain(isComponent);

        scopeComponentsGroup = with(components).group(new DIUtils.ScopeGroupCondition());
        for (Scope scope : Scope.values()) {
            LambdaList<Class<?>> scopeComponents = scopeComponentsGroup.find(scope);
            debug("scope: " + scope + " scopeComponents: " + scopeComponents);
        }

        pluginInjector = new Injector(scopeComponentsGroup.find(Scope.PLUGIN));

        pageInjectorMap = Collections.synchronizedMap(new IdentityHashMap<IWorkbenchPage, Injector>());

        windowListener = new WindowListener();
        PlatformUI.getWorkbench().addWindowListener(windowListener);
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            windowListener.alreadyOpened(window);
        }
    }

    public Injector getInjector(Component.Scope scope) {
        switch (scope) {
        case PLUGIN:
            return pluginInjector;
        case PAGE:
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            Injector pageInjector = pageInjectorMap.get(page);
            if (pageInjector == null) {
                throw new IllegalStateException("unable to locate pageInjector for page: " + page);
            }
            return pageInjector;
        default:
            throw new IllegalArgumentException("unexpected scope: " + scope);
        }
    }

    private void close() {
        PlatformUI.getWorkbench().removeWindowListener(windowListener);
    }

    public static void start(BundleContext bundleContext, String packagePrefix) {
        instance = new DIManager(bundleContext, packagePrefix);
    }

    public static void stop() {
        instance.close();
        instance = null;
    }

    public static DIManager getDefault() {
        return instance;
    }

    private static void debug(String message) {
        debug(message);
    }
}
