package lets.cool.util.logging;

/**
 * FINEST  -> TRACE
 * FINER   -> DEBUG
 * FINE    -> INFO
 * CONFIG  -> CONFIG
 * INFO    -> NOTICE
 * WARNING -> WARN
 * SEVERE  -> ERROR
 */
public enum Level {
    ERROR(LVL.ERROR),
    WARN(LVL.WARN),
    NOTICE(LVL.NOTICE),
    CONFIG(LVL.CONFIG),
    INFO(LVL.INFO),
    DEBUG(LVL.DEBUG),
    TRACE(LVL.TRACE),
    OFF(LVL.OFF);

    final static String ERROR_NAME      = "ERROR";
    final static String WARN_NAME       = "WARN";
    final static String NOTICE_NAME     = "NOTICE";
    final static String CONFIG_NAME     = "CONFIG";
    final static String INFO_NAME       = "INFO";
    final static String DEBUG_NAME      = "DEBUG";
    final static String TRACE_NAME      = "TRACE";
    final static String OFF_NAME        = "OFF";

    final protected java.util.logging.Level level;
    Level(java.util.logging.Level lvl) {
        level = lvl;
    }

    static class LVL extends java.util.logging.Level {

        static LVL ERROR = new LVL(ERROR_NAME, java.util.logging.Level.SEVERE.intValue());
        static LVL WARN = new LVL(WARN_NAME, java.util.logging.Level.WARNING.intValue());
        static LVL NOTICE = new LVL(NOTICE_NAME, java.util.logging.Level.INFO.intValue());
        static LVL CONFIG = new LVL(CONFIG_NAME, java.util.logging.Level.CONFIG.intValue());
        static LVL INFO = new LVL(INFO_NAME, java.util.logging.Level.FINE.intValue());
        static LVL DEBUG = new LVL(DEBUG_NAME, java.util.logging.Level.FINER.intValue());
        static LVL TRACE = new LVL(TRACE_NAME, java.util.logging.Level.FINEST.intValue());
        static LVL OFF = new LVL(OFF_NAME, java.util.logging.Level.OFF.intValue());

        private LVL(String name, int value) {
            super(name, value);
        }
    }
}
