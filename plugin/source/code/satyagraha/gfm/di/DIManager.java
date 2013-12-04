package code.satyagraha.gfm.di;

import static ch.lambdaj.collection.LambdaCollections.with;
import static code.satyagraha.gfm.di.DIUtils.getBundleClasses;
import static code.satyagraha.gfm.di.DIUtils.ComponentMatcher.isComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.BundleContext;

import ch.lambdaj.collection.LambdaGroup;
import ch.lambdaj.collection.LambdaList;
import code.satyagraha.gfm.di.Component.Scope;

public class DIManager {

    private static DIManager instance;

    private final IWorkbench workbench;
    private final boolean debugging;
    private final LambdaGroup<Class<?>> scopeComponentsGroup;
    private final InjectorImpl pluginInjector;
    private final Map<IWorkbenchPage, Injector> pageInjectorMap;
    private final WindowListener windowListener;

    // /////////////////////////////////////////////////////////////////////////
    // support classes
    // /////////////////////////////////////////////////////////////////////////

    private class PageListener implements IPageListener {

        void observing(IWorkbenchPage page) {
            debug("PageListener.observing: " + page);
            pageOpened(page);
        }

        @Override
        public void pageOpened(IWorkbenchPage page) {
            debug("PageListener.pageOpened: " + page);
            if (!pageInjectorMap.containsKey(page)) {
                Injector pageInjector = new InjectorImpl(pluginInjector, scopeComponentsGroup.find(Scope.PAGE));
                pageInjector.addInstance(page);
                pageInjectorMap.put(page, pageInjector);
            }
        }

        @Override
        public void pageActivated(IWorkbenchPage page) {
            debug("PageListener.pageActivated: " + page);
        }

        @Override
        public void pageClosed(IWorkbenchPage page) {
            debug("PageListener.pageClosed: " + page);
            Injector pageInjector = pageInjectorMap.get(page);
            if (pageInjector != null) {
                pageInjector.close();
                pageInjectorMap.remove(page);
            }
        }

    }

    private class WindowListener implements IWindowListener {

        private final Map<IWorkbenchWindow, PageListener> pageListeners;

        WindowListener() {
            pageListeners = new IdentityHashMap<IWorkbenchWindow, PageListener>();
        }

        void observing(IWorkbenchWindow window) {
            debug("WindowListener.observing: " + window);
            windowOpened(window);
        }

        @Override
        public void windowOpened(IWorkbenchWindow window) {
            debug("WindowListener.windowOpened: " + window);
            if (!pageListeners.containsKey(window)) {
                PageListener pageListener = new PageListener();
                pageListeners.put(window, pageListener);
                window.addPageListener(pageListener);
                for (IWorkbenchPage page : window.getPages()) {
                    pageListener.observing(page);
                }
            }
        }

        @Override
        public void windowActivated(IWorkbenchWindow window) {
            debug("WindowListener.windowActivated: " + window);
        }

        @Override
        public void windowDeactivated(IWorkbenchWindow window) {
            debug("WindowListener.windowDeactivated: " + window);
        }

        @Override
        public void windowClosed(IWorkbenchWindow window) {
            debug("WindowListener.windowClosed: " + window);
            PageListener pageListener = pageListeners.get(window);
            if (pageListener != null) {
                pageListeners.remove(window);
                window.removePageListener(pageListener);
            }
        }

        void close() {
            for (Entry<IWorkbenchWindow, PageListener> entry : pageListeners.entrySet()) {
                IWorkbenchWindow workbenchWindow = entry.getKey();
                PageListener pageListener = entry.getValue();
                workbenchWindow.removePageListener(pageListener);
            }
            pageListeners.clear();
        }

    }

    // /////////////////////////////////////////////////////////////////////////
    // class implementation
    // /////////////////////////////////////////////////////////////////////////

    private DIManager(IWorkbench workbench, BundleContext bundleContext, String packagePrefix, boolean debugging) {
        this.workbench = workbench;
        this.debugging = debugging;

        Collection<Class<?>> components = with(getBundleClasses(bundleContext.getBundle(), packagePrefix)).retain(isComponent);

        scopeComponentsGroup = with(components).group(new DIUtils.ScopeGroupCondition());
        for (Scope scope : Scope.values()) {
            LambdaList<Class<?>> scopeComponents = scopeComponentsGroup.find(scope);
            debug("scope: " + scope + " scopeComponents: " + scopeComponents);
        }

        pluginInjector = new InjectorImpl(scopeComponentsGroup.find(Scope.PLUGIN));
        pluginInjector.addInstance(workbench);
        pluginInjector.addInstance(bundleContext);

        pageInjectorMap = Collections.synchronizedMap(new IdentityHashMap<IWorkbenchPage, Injector>());

        windowListener = new WindowListener();
        workbench.addWindowListener(windowListener);
        for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
            windowListener.observing(window);
        }
    }

    public Injector getInjector(Component.Scope scope) {
        if (scope == null) {
            throw new IllegalArgumentException("null scope");
        }
        switch (scope) {
        case PLUGIN:
            return pluginInjector;

        case PAGE:
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            if (workbenchWindow == null) {
                throw new IllegalStateException("no active workbench window");
            }
            IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
            if (workbenchPage == null) {
                throw new IllegalStateException("no active workbench page");
            }
            Injector pageInjector = pageInjectorMap.get(workbenchPage);
            if (pageInjector == null) {
                throw new IllegalStateException("unable to locate pageInjector for page: " + workbenchPage);
            }
            return pageInjector;

        default:
            throw new IllegalArgumentException("unexpected scope: " + scope);
        }
    }

    private void close() {
        if (windowListener != null) {
            windowListener.close();
            workbench.removeWindowListener(windowListener);
        }
        if (pluginInjector != null) {
            pluginInjector.close();
        }
        for (Injector pageInjector: pageInjectorMap.values()) {
            pageInjector.close();
        }
        pageInjectorMap.clear();
    }

    private void debug(String message) {
        if (debugging) {
            System.out.println(message);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // static methods
    // /////////////////////////////////////////////////////////////////////////

    public static void start(IWorkbench workbench, BundleContext bundleContext, String packagePrefix, boolean debugging) {
        instance = new DIManager(workbench, bundleContext, packagePrefix, debugging);
    }

    public static void stop() {
        instance.close();
        instance = null;
    }

    public static DIManager getDefault() {
        return instance;
    }

}
