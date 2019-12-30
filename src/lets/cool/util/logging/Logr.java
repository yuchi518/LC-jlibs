/*
 * LC-jlibs, lets.cool java libraries
 * Copyright (C) 2015-2018 Yuchi Chen (yuchi518@gmail.com)

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation. For the terms of this
 * license, see <http://www.gnu.org/licenses>.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package lets.cool.util.logging;

import org.checkerframework.checker.formatter.qual.FormatMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.logging.*;

/*
 *  TODO: 搞清楚 Logger 與 Handler (ConsoleHandler, FileHandler) 設定 Log level 的優先順序，
 *   目前執行時期改變 Level 可能會有不可預期的行為模式出現。
 */
public class Logr {

    static final HashMap<String, Logr> _logrs = new HashMap<>();
    static final HashSet<Handler> fileHandlers = new HashSet<>();
    static String pauseAllButExcludedNames = null;
    static Level defaultLevel = Level.INFO;

    public static Logr logger() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i=0; i<stackTrace.length; i++)
        {
            if (stackTrace[i].getClassName().equals(Logr.class.getName()))
            {
                String name = stackTrace[i+1].getClassName();

                Logr logr;
                synchronized (_logrs) {
                    logr = _logrs.get(name);
                    if (logr != null) return logr;
                    logr = new Logr(Logger.getLogger(name));
                    final Logr f_logr = logr;
                    _logrs.put(name, logr);
                    logr.logger.setUseParentHandlers(false);
                    logr.logger.setLevel(defaultLevel.level);
                    logr.logger.setFilter(record -> f_logr.isLoggable(record));

                    if (fileHandlers.size()==0) {
                        Handler handler = new ConsoleHandler() {
                            public void publish(LogRecord record) {
                                f_logr.publish(record, this.getFormatter());
                            }
                        };
                        handler.setLevel(defaultLevel.level);
                        fileHandlers.add(handler);
                    }

                    for (Handler handler: fileHandlers) {
                        logr.logger.addHandler(handler);
                    }
                }

                logr.configf("Logr for %s enabled\n", name);

                return logr;
            }
        }

        throw new RuntimeException("Can't not create logger, why ???");
    }

    /*public static void applyConfiguration(Consumer<Logr> setting) {

    }*/

    public static void pauseAll() {
        synchronized (_logrs) {
            pauseAllButExcludedNames = "";
        }
    }

    public static void pauseAllBut(Logr onlyOne) {
        pauseAllBut(onlyOne.logger.getName());
    }

    public static void pauseAllBut(String ... excludedNames) {
        synchronized (_logrs) {
            pauseAllButExcludedNames =  String.join(",", excludedNames) + ",";
        }
    }

    public static void continueAll() {
        synchronized (_logrs) {
            pauseAllButExcludedNames = null;
        }
    }

    public static void setDefaultLevel(Level level) {
        defaultLevel = level;
    }

    public static void setLevelToAllLogrs(Level level) {
        setDefaultLevel(level);
        synchronized (_logrs) {
            for (Logr logr: _logrs.values()) {
                logr.setLevel(level);
            }
        }
    }

    public static FileHandler setLogFilename(String filename, boolean append) {
        try {
            FileHandler fileHandler = new FileHandler(filename, append);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(defaultLevel.level);
            fileHandlers.add(fileHandler);

            synchronized (_logrs) {
                for (Logr logr: _logrs.values()) {
                    logr.logger.addHandler(fileHandler);
                }
            }
            return fileHandler;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeHandler(Handler handler) {
        synchronized (_logrs) {
            for (Logr logr: _logrs.values()) {
                logr.logger.removeHandler(handler);
            }
            fileHandlers.remove(handler);
        }
    }

    final protected Logger logger;
    protected boolean paused;

    public Logr(Logger logger) {
        this.logger = logger;
        this.paused = false;
    }

    public void setLevel(Level level) {
        logger.setLevel(level.level);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isLoggable(Level level) {
        return logger.isLoggable(level.level);
    }

    boolean isLoggable(LogRecord record) {
        return (pauseAllButExcludedNames ==null || pauseAllButExcludedNames.contains(this.logger.getName()+",")) && !paused;
    }

    void publish(LogRecord record, Formatter formatter) {
        String msg = formatter.formatMessage(record);
        switch (record.getLevel().getName())
        {
            case Level.ERROR_NAME:
            case Level.WARN_NAME:
            case Level.NOTICE_NAME: {
                System.err.println(msg);
                break;
            }
            case Level.CONFIG_NAME:
            case Level.INFO_NAME:
            case Level.DEBUG_NAME:
            case Level.TRACE_NAME: {
                System.out.println(msg);
                break;
            }
            case Level.OFF_NAME:
            default: {
                // ignore
                //System.err.println(msg);
                break;
            }
        }
    }

    // universal log
    class Caller {
        String sourceClassName;
        String sourceMethodName;
    }

    private Caller inferCaller() {
        StackTraceElement elements[] = new Throwable().getStackTrace();
        int i;
        boolean isEntered = false;
        for (i=0; i<elements.length; i++) {
            String cla_name = elements[i].getClassName();
            if (!cla_name.equals(Logr.class.getName())) {
                if (isEntered) {
                    Caller clr = new Caller();
                    clr.sourceClassName = cla_name;
                    clr.sourceMethodName = elements[i].getMethodName();
                    return clr;
                }
                break;
            } else {
                isEntered = true;
            }
        }
        Caller clr = new Caller();
        clr.sourceClassName = "N/A";
        clr.sourceMethodName = "N/A";
        return clr;
    }


    public void log(Level level, String msg) {
        Caller clr = inferCaller();
        this.logger.logp(level.level, clr.sourceClassName, clr.sourceMethodName, msg);
    }

    public void log(Level level, Supplier<String> msgSupplier) {
        Caller clr = inferCaller();
        this.logger.logp(level.level, clr.sourceClassName, clr.sourceMethodName, msgSupplier);
    }

    public void log(Level level, String msg, Object param1) {
        Caller clr = inferCaller();
        this.logger.logp(level.level, clr.sourceClassName, clr.sourceMethodName, msg, param1);
    }

    public void log(Level level, String msg, Object params[]) {
        Caller clr = inferCaller();
        this.logger.logp(level.level, clr.sourceClassName, clr.sourceMethodName, msg, params);
    }

    public void log(Level level, String msg, Throwable thrown) {
        Caller clr = inferCaller();
        this.logger.logp(level.level, clr.sourceClassName, clr.sourceMethodName, msg, thrown);
    }

    public void log(Level level, Throwable thrown, Supplier<String> msgSupplier) {
        Caller clr = inferCaller();
        this.logger.logp(level.level, clr.sourceClassName, clr.sourceMethodName, thrown, msgSupplier);
    }

    @FormatMethod
    public void logf(Level level, String format, Object ... args) {
        String msg = String.format(format, args);
        Caller clr = inferCaller();
        this.logger.logp(level.level, clr.sourceClassName, clr.sourceMethodName, msg);
    }

    // Exception log

    public void exception(String msg) {
        log(Level.ERROR, msg);
        throw new RuntimeException(msg);
    }

    public void exception(Object obj) {
        log(Level.ERROR, obj.toString());
        throw new RuntimeException(obj.toString());
    }

    public void exception(Supplier<String> msgSupplier) {
        log(Level.ERROR, msgSupplier);
        throw new RuntimeException(msgSupplier.get());
    }

    /*public void exception(String msg, Object param1) {
        log(Level.ERROR, msg, param1);
    }

    public void exception(String msg, Object params[]) {
        log(Level.ERROR, msg, params);
    }*/

    public void exception(String msg, Throwable thrown) {
        log(Level.ERROR, msg, thrown);
        throw new RuntimeException(msg, thrown);
    }

    public void exception(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.ERROR, thrown, msgSupplier);
        throw new RuntimeException(msgSupplier.get(), thrown);
    }

    @FormatMethod
    public void exceptionf(String format, Object ... args) {
        logf(Level.ERROR, format, args);
        throw new RuntimeException(String.format(format, args));
    }

    // ERROR log

    public void error(String msg) {
        log(Level.ERROR, msg);
    }

    public void error(Object obj) {
        log(Level.ERROR, obj.toString());
    }

    public void error(Supplier<String> msgSupplier) {
        log(Level.ERROR, msgSupplier);
    }

    public void error(String msg, Object param1) {
        log(Level.ERROR, msg, param1);
    }

    public void error(String msg, Object params[]) {
        log(Level.ERROR, msg, params);
    }

    public void error(String msg, Throwable thrown) {
        log(Level.ERROR, msg, thrown);
    }

    public void error(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.ERROR, thrown, msgSupplier);
    }

    @FormatMethod
    public void errorf(String format, Object ... args) {
        logf(Level.ERROR, format, args);
    }

    // WARN

    public void warn(String msg) {
        log(Level.WARN, msg);
    }

    public void warn(Object obj) {
        log(Level.WARN, obj.toString());
    }

    public void warn(Supplier<String> msgSupplier) {
        log(Level.WARN, msgSupplier);
    }

    public void warn(String msg, Object param1) {
        log(Level.WARN, msg, param1);
    }

    public void warn(String msg, Object params[]) {
        log(Level.WARN, msg, params);
    }

    public void warn(String msg, Throwable thrown) {
        log(Level.WARN, msg, thrown);
    }

    public void warn(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.WARN, thrown, msgSupplier);
    }

    @FormatMethod
    public void warnf(String format, Object ... args) {
        logf(Level.WARN, format, args);
    }

    // NOTICE

    public void notice(String msg) {
        log(Level.NOTICE, msg);
    }

    public void notice(Object obj) {
        log(Level.NOTICE, obj.toString());
    }

    public void notice(Supplier<String> msgSupplier) {
        log(Level.NOTICE, msgSupplier);
    }

    public void notice(String msg, Object param1) {
        log(Level.NOTICE, msg, param1);
    }

    public void notice(String msg, Object params[]) {
        log(Level.NOTICE, msg, params);
    }

    public void notice(String msg, Throwable thrown) {
        log(Level.NOTICE, msg, thrown);
    }

    public void notice(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.NOTICE, thrown, msgSupplier);
    }

    @FormatMethod
    public void noticef(String format, Object ... args) {
        logf(Level.NOTICE, format, args);
    }

    // CONFIG

    public void config(String msg) {
        log(Level.CONFIG, msg);
    }

    public void config(Object obj) {
        log(Level.CONFIG, obj.toString());
    }

    public void config(Supplier<String> msgSupplier) {
        log(Level.CONFIG, msgSupplier);
    }

    public void config(String msg, Object param1) {
        log(Level.CONFIG, msg, param1);
    }

    public void config(String msg, Object params[]) {
        log(Level.CONFIG, msg, params);
    }

    public void config(String msg, Throwable thrown) {
        log(Level.CONFIG, msg, thrown);
    }

    public void config(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.CONFIG, thrown, msgSupplier);
    }

    @FormatMethod
    public void configf(String format, Object ... args) {
        logf(Level.CONFIG, format, args);
    }

    // INFO

    public void info(String msg) {
        log(Level.INFO, msg);
    }

    public void info(Object obj) {
        log(Level.INFO, obj.toString());
    }

    public void info(Supplier<String> msgSupplier) {
        log(Level.INFO, msgSupplier);
    }

    public void info(String msg, Object param1) {
        log(Level.INFO, msg, param1);
    }

    public void info(String msg, Object params[]) {
        log(Level.INFO, msg, params);
    }

    public void info(String msg, Throwable thrown) {
        log(Level.INFO, msg, thrown);
    }

    public void info(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.INFO, thrown, msgSupplier);
    }

    @FormatMethod
    public void infof(String format, Object ... args) {
        logf(Level.INFO, format, args);
    }

    // DEBUG

    public void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    public void debug(Object obj) {
        log(Level.DEBUG, obj.toString());
    }

    public void debug(Supplier<String> msgSupplier) {
        log(Level.DEBUG, msgSupplier);
    }

    public void debug(String msg, Object param1) {
        log(Level.DEBUG, msg, param1);
    }

    public void debug(String msg, Object params[]) {
        log(Level.DEBUG, msg, params);
    }

    public void debug(String msg, Throwable thrown) {
        log(Level.DEBUG, msg, thrown);
    }

    public void debug(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.DEBUG, thrown, msgSupplier);
    }

    @FormatMethod
    public void debugf(String format, Object ... args) {
        logf(Level.DEBUG, format, args);
    }

    // TRACE

    public void trace(String msg) {
        log(Level.TRACE, msg);
    }

    public void trace(Object obj) {
        log(Level.TRACE, obj.toString());
    }

    public void trace(Supplier<String> msgSupplier) {
        log(Level.TRACE, msgSupplier);
    }

    public void trace(String msg, Object param1) {
        log(Level.TRACE, msg, param1);
    }

    public void trace(String msg, Object params[]) {
        log(Level.TRACE, msg, params);
    }

    public void trace(String msg, Throwable thrown) {
        log(Level.TRACE, msg, thrown);
    }

    public void trace(Throwable thrown, Supplier<String> msgSupplier) {
        log(Level.TRACE, thrown, msgSupplier);
    }

    @FormatMethod
    public void tracef(String format, Object ... args) {
        logf(Level.TRACE, format, args);
    }

}
