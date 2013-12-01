package code.satyagraha.gfm.viewer.views;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import code.satyagraha.gfm.viewer.model.api.MarkdownBrowser;

public class MarkdownBrowserDefault implements MarkdownBrowser, ProgressListener {

    private Browser browser;
    private File htFile;
    private Integer lastScroll;

    private static Logger LOGGER = Logger.getLogger(MarkdownBrowserDefault.class.getPackage().getName());

    public MarkdownBrowserDefault(Composite parent) {
        browser = new Browser(parent, SWT.NONE);
        LOGGER.fine("browser type: " + browser.getBrowserType());
        browser.addProgressListener(this);
//        setDropTarget();
    }

//    (experimental)
//    private void setDropTarget() {
//        Activator.debug("");
//        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
//        DropTarget dropTarget = new DropTarget(browser.getParent(), operations);
//        Transfer[] transferTypes = new Transfer[] { FileTransfer.getInstance() };
//        dropTarget.setTransfer(transferTypes);
//        
//        DropTargetAdapter dropListener = new DropTargetAdapter() {
//
//            @Override
//            public void drop(DropTargetEvent event) {
//                Activator.debug("");
//                if (!FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
//                    return;
//                }
//                String[] passed = (String[]) event.data;
//                if (passed.length != 1) {
//                    return;
//                }
//                File file = new File(passed[0]);
//                handleDrop(file);
//            }
//        };
//        dropTarget.addDropListener(dropListener);
//    }

    @Override
    public void showHtmlFile(File htFileNew) {
        LOGGER.fine("htFileNew: " + htFileNew.getPath());
        htFile = htFileNew;
        
        // determine whether to refresh or load (user may have navigated away)
        URI currentUri;
        try {
            currentUri = new URI(browser.getUrl());
        } catch (URISyntaxException e) {
            currentUri = null;
        }
        URI newUri = htFile.toURI();
        boolean sameUri = currentUri != null && currentUri.equals(newUri);
        
        if (sameUri) {
            LOGGER.fine("refreshing: "  + currentUri);
            lastScroll = getScrollTop();
            browser.refresh();
        } else {
            LOGGER.fine("currentUri: "  + currentUri);
            LOGGER.fine("loading: "  + newUri);
            lastScroll = null;
            browser.setUrl(newUri.toString());
        }
    }

    @Override
    public File getHtFile() {
        return htFile;
    }
    
    @Override
    public void changed(ProgressEvent event) {
        // no-op
    }

    @Override
    public void completed(ProgressEvent event) {
        LOGGER.fine("lastScroll: " + lastScroll);
        if (lastScroll != null) {
            browser.execute(String.format("setDocumentScrollTop(%d);", lastScroll));
        }
        restoreCursor();
    }

    private Integer getScrollTop() {
        Object position;
        try {
            position = browser.evaluate("return getDocumentScrollTop();");
        } catch (SWTException e) {
            LOGGER.log(Level.WARNING, "unable to evaluate getDocumentScrollTop()", e);
            return null;
        }
        LOGGER.fine("position: " + position);
        return position != null ? ((Double) position).intValue() : null;
    }

    @Override
    public void forward() {
        browser.forward();
    }

    @Override
    public void back() {
        browser.back();
    }

    @Override
    public void dispose() {
        browser.dispose();
        browser = null;
    }

//    public abstract void handleDrop(File file);

    private void restoreCursor() {
        LOGGER.fine("");
        // the following code is a work-around for the problem of disappearing
        // cursor on Windows
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor instanceof ITextEditor) {
            ITextEditor textEditor = (ITextEditor) editor;
            IAction action = textEditor.getAction(ITextEditorActionDefinitionIds.TOGGLE_OVERWRITE);
            if (action != null) {
                action.run();
                action.run();
            }
        }
    }

}
