package fr.vmarchaud.mineweb.utils;

import java.text.*;
import java.util.logging.*;
import java.util.*;

public class CustomLogFormatter extends Formatter
{
    private final DateFormat dateFormat;
    
    public CustomLogFormatter() {
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    }
    
    @Override
    public String format(final LogRecord record) {
        return String.format("%s - [%s.%s] - %s - %s%s", this.dateFormat.format(new Date()), record.getSourceClassName(), record.getSourceMethodName(), record.getLevel(), this.formatMessage(record), System.lineSeparator());
    }
}
