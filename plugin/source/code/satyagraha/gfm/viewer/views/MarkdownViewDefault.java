package code.satyagraha.gfm.viewer.views;

import java.util.logging.Logger;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import code.satyagraha.gfm.di.Component.Scope;
import code.satyagraha.gfm.di.DIManager;
import code.satyagraha.gfm.di.Injector;
import code.satyagraha.gfm.viewer.model.api.MarkdownView;
import code.satyagraha.gfm.viewer.model.api.ViewerModel;

public class MarkdownViewDefault extends ViewPart implements MarkdownView {

    private static Logger LOGGER = Logger.getLogger(MarkdownViewDefault.class.getPackage().getName());

    private ViewerModel model;

    @Override
    public void createPartControl(Composite parent) {
        LOGGER.fine("");

        Injector injector = DIManager.getDefault().getInjector(Scope.PAGE);
        
        MarkdownBrowserDefault browser = new MarkdownBrowserDefault(parent);
        injector.addInstance(browser);
        model = injector.getInstance(ViewerModel.class);
        model.start();
    }

    @Override
    public void setFocus() {
        LOGGER.fine("");
    }

    @Override
    public void dispose() {
        LOGGER.fine("");
        model.stop();
        // browser.dispose();
        super.dispose();
    }

}
