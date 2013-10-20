package code.satyagraha.gfm.ui.impl;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.collection.LambdaCollections.with;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.hamcrest.Matchers;

public class ViewLocator {
    
    /**
     * Find a view implementing a specified interface.
     * 
     * @param type the interface
     * @return the view (or null)
     */
    @SuppressWarnings("unchecked")
    public static <T> T findViewImplementing(Class<T> type) {
        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewReference[] viewReferences = workbenchPage.getViewReferences();
        IWorkbenchPart part = with(viewReferences).extract(on(IViewReference.class).getPart(false)).first(Matchers.instanceOf(type));
        return (T)part;
    }


}
