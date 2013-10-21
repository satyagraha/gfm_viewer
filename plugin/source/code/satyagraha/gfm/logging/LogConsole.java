package code.satyagraha.gfm.logging;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class LogConsole {
    
    private static LogConsole instance;

    private final String name;
    private StreamHandler handler;
    
    private static class FlushingStreamHandler extends StreamHandler {
        
        public FlushingStreamHandler(OutputStream outputStream, Formatter formatter) {
            super(outputStream, formatter);
        }
        
        @Override
        public synchronized void publish(LogRecord paramLogRecord) {
            super.publish(paramLogRecord);
            flush();
        }
    }
    
    public LogConsole(String name) {
        this.name = name;
    }

    public Handler createHandler(Formatter formatter) {
        MessageConsole console = new MessageConsole(name, null);
        console.activate();
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ console });
        MessageConsoleStream consoleStream = console.newMessageStream();
        handler = new FlushingStreamHandler(consoleStream, formatter);
        return handler;
    }
    
    private void close() {
        handler.close();
    }
    
    public static void start(String name) {
        instance = new LogConsole(name);
    }
    
    public static void stop() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public static LogConsole getInstance() {
        return instance;
    }
}
