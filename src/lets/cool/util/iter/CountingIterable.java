package lets.cool.util.iter;

public interface CountingIterable<T> extends SizeAwareIterable<T>, IndexAwareIterable<T>, ProgressAwareIterable<T> {
    CountingIterator<T> iterator();
}


