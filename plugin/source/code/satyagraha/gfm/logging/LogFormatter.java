package code.satyagraha.gfm.logging;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    
    @Override
    public String format(LogRecord record) {
        return new Date(record.getMillis()) + " " + record.getSourceClassName() + " " + record.getSourceMethodName() + " : " + formatMessage(record) + "\n";
    }
}
