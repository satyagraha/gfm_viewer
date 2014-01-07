package code.satyagraha.gfm.ui.impl;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.collection.LambdaCollections.with;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.hamcrest.Matchers;

public class ViewManager {

    /**
     * Find a view implementing a specified interface.
     * 
     * @param type
     *            the interface
     * @return the view (or null)
     */
    @SuppressWarnings("unchecked")
    public static <T> T findViewImplementing(Class<T> type) {
        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewReference[] viewReferences = workbenchPage.getViewReferences();
        IWorkbenchPart part = with(viewReferences).extract(on(IViewReference.class).getPart(false)).first(Matchers.instanceOf(type));
        return (T) part;
    }

    /**
     * Activate a view by id.
     * 
     * @param event
     * @param viewId
     * @throws ExecutionException
     */
    public static void activateView(ExecutionEvent event, String viewId) throws ExecutionException {
        try {
            IViewPart view = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(viewId);
            HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().activate(view);
        } catch (PartInitException e) {
            throw new ExecutionException("failed to show view", e);
        }
    }

}
