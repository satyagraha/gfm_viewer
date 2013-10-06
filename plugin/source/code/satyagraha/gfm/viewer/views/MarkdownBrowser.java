package code.satyagraha.gfm.viewer.views;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;

import code.satyagraha.gfm.viewer.plugin.Activator;

public abstract class MarkdownBrowser implements ProgressListener {

    private Browser browser;
    private File lastHtFile;
    private Integer lastScroll;

    public MarkdownBrowser(Composite parent) {
        browser = new Browser(parent, SWT.NONE);
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

    public void showHtmlFile(File htFile) {
        Activator.debug(String.format("htFile: %s", htFile.getPath()));
        lastScroll = htFile.equals(lastHtFile) ? getScrollTop() : null;
        Activator.debug(String.format("lastScroll: %s", lastScroll));
        lastHtFile = htFile;
        browser.setUrl(htFile.toURI().toString());
    }

    @Override
    public void changed(ProgressEvent event) {
        // Activator.debug("");
    }

    @Override
    public void completed(ProgressEvent event) {
        Activator.debug(String.format("lastScroll: %s", lastScroll));
        if (lastScroll != null) {
            browser.execute(String.format("setDocumentScrollTop(%d);", lastScroll));
        }
    }

    private Integer getScrollTop() {
        Object position;
        try {
            position = browser.evaluate("return getDocumentScrollTop();");
        } catch (SWTException e) {
            Activator.debug(String.format("%s - %s", e.getClass().getCanonicalName(), e.getMessage()));
            return null;
        }
        Activator.debug(String.format("position: %s", position));
        return position != null ? ((Double) position).intValue() : null;
    }

    public void forward() {
        browser.forward();
    }

    public void back() {
        browser.back();
    }

    public void dispose() {
        browser.dispose();
        browser = null;
    }

    public abstract void handleDrop(File file);

}
