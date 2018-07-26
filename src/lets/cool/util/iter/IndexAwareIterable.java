package lets.cool.util.iter;

public interface IndexAwareIterable<T> extends Iterable<T> {
    IndexAwareIterator<T> iterator();
}
