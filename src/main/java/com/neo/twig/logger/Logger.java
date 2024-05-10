package com.neo.twig.logger;

import com.neo.twig.Engine;

@SuppressWarnings("unused")
public class Logger {
    private static Level loggingLevel = Level.INFO;
    private String name;
    private String spaceName;
    private boolean isEngineClass;

    private Logger() {

    }

    public static Logger getFor(Class<?> type) {
        Logger logger = new Logger();

        logger.name = type.getSimpleName();
        if (type.getPackageName().startsWith("com.neo.twig")) {
            logger.spaceName = "Twig";
        } else if (!Engine.getConfig().appConfig().name.isEmpty()) {
            logger.spaceName = Engine.getConfig().appConfig().name;
        } else {
            logger.spaceName = "Unnamed Runtime";
        }

        return logger;
    }

    public static void setLoggingLevel(Logger.Level level) {
        loggingLevel = level;
    }

    public void logVerbose(String message) {
        if (loggingLevel.ordinal() < Level.INFO.ordinal()) {
            System.out.printf("[%s - %s - Verbose]: %s\n", spaceName, name, message);
        }
    }

    public void logInfo(String message) {
        if (loggingLevel.ordinal() < Level.DEBUG.ordinal())
            System.out.printf("[%s - %s - Info]: %s\n", spaceName, name, message);
    }

    public void logDebug(String message) {
        if (loggingLevel.ordinal() < Level.WARNING.ordinal())
            System.out.printf("[%s - %s - Debug]: %s\n", spaceName, name, message);
    }

    public void logWarning(String message) {
        if (loggingLevel.ordinal() < Level.ERROR.ordinal())
            System.out.printf("[%s - %s - Warning]: %s\n", spaceName, name, message);
    }

    public void logError(String message) {
        if (loggingLevel.ordinal() < Level.FATAL.ordinal())
            System.out.printf("[%s - %s - Error]: %s\n", spaceName, name, message);
    }

    public void logFatal(String message) {
        System.out.printf("[%s - %s - Fatal]: %s\n", spaceName, name, message);
    }

    public enum Level {
        VERBOSE,
        INFO,
        DEBUG,
        WARNING,
        ERROR,
        FATAL
    }
}
