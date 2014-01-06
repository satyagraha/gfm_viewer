package code.satyagraha.gfm.viewer.views;

import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

import code.satyagraha.gfm.viewer.plugin.Activator;

/**
 * Provides information about running environment
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class InformationTest {

    @Test
    public void showSWTBotDependencies() {
        log("commencing showSWTBotDependencies");
        Bundle bundle = FrameworkUtil.getBundle(SWTBot.class);
        BundleWiring wiring = bundle.adapt(BundleWiring.class);
        List<BundleWire> requiredWires = wiring.getRequiredWires(null);
        for (BundleWire requiredWire : requiredWires) {
            log("requiredWire: " + requiredWire);
        }
        log("completed showSWTBotDependencies");
    }
    
    public static void log(String msg) {
        log(msg, null);
    }
    
    public static void log(String msg, Exception e) {
        getPlugin().getLog().log(new Status(Status.INFO, getPlugin().getBundle().getSymbolicName(), Status.OK, msg, e));
    }

    private static Plugin getPlugin() {
        return Activator.getDefault();
    }
    
}
