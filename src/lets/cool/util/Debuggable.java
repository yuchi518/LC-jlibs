package lets.cool.util;

public interface Debuggable {
    default void debug(Object ... args) {
        // not support, but don't throw exception.
    }
}
