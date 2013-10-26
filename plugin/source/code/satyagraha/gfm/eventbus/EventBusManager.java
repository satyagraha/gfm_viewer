package code.satyagraha.gfm.eventbus;

import java.util.List;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventServiceExistsException;
import org.bushe.swing.event.EventServiceLocator;
import org.bushe.swing.event.ThreadSafeEventService;
import org.eclipse.swt.widgets.Display;

public class EventBusManager {

    private static class EclipseEventService extends ThreadSafeEventService {

        @Override
        @SuppressWarnings("rawtypes")
        protected void publish(final Object event, final String topic, final Object eventObj, final List subscribers, final List vetoSubscribers,
                final StackTraceElement[] callingStack) {

            Display.getDefault().asyncExec(new Runnable() {
                
                @Override
                public void run() {
                    EclipseEventService.super.publish(event, topic, eventObj, subscribers, vetoSubscribers, callingStack);
                }
            });

        }
    }

    public static void start() {
        try {
            EventServiceLocator.setEventService(EventServiceLocator.SERVICE_NAME_EVENT_BUS, new EclipseEventService());
        } catch (EventServiceExistsException e) {
            throw new RuntimeException(e);
        }
        EventBus.publish(new String());
    }

    public static void stop() {

    }

}
