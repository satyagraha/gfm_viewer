package code.satyagraha.gfm.viewer.views;

import java.io.File;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.di.Injector;
import code.satyagraha.gfm.viewer.model.api.MarkdownBrowser;
import code.satyagraha.gfm.viewer.model.api.MarkdownView;
import code.satyagraha.gfm.viewer.model.api.ViewerModel;

public class MarkdownViewDefault extends ViewPart implements MarkdownView {

    private static Logger LOGGER = Logger.getLogger(MarkdownViewDefault.class.getPackage().getName());

    @Inject
    private ViewerModel model;

    private Injector injector;

    private MarkdownBrowser browser;

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        injector = DIManager.getDefault().getInjector(Scope.PAGE);
    }
    
    @Override
    public void createPartControl(Composite parent) {
        LOGGER.fine("");
        browser = new MarkdownBrowserDefault(parent);
        injector.addInstance(browser);
        injector.addInstance(this);
        injector.inject(this);
        model.start();
    }

    @Override
    public void setFocus() {
        LOGGER.fine("");
    }

    @Override
    public void dispose() {
        LOGGER.fine("");
        if (model != null) {
            model.stop();
            injector.removeInstance(this);
            injector.removeInstance(browser);
            browser = null;
            model = null;
        };
        super.dispose();
    }

    @Override
    public void nowShowing(File mdFile, boolean upToDate) {
        String displayName = (upToDate ? "" : "*") + FilenameUtils.getBaseName(mdFile.getName());
        setPartName(displayName);
    }
}
