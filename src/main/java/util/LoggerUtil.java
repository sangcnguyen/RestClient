package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    private LoggerUtil() {
        throw new IllegalStateException("Can't instantiate the LoggerUtil class");
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }
}
