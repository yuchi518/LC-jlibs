package lets.cool.util.iter;

import java.util.Collection;
import java.util.Iterator;

public interface PairIterator<T> extends Iterator<PairIterator.Pair<T>> {
    class Pair<T> {
        final public T a;
        final public T b;

        public Pair(T a, T b) {
            this.a = a;
            this.b = b;
        }
    }

    static <T> PairIterator<T> withLast(Collection<T> fromCollection) {
        return withLast(fromCollection.iterator());
    }

    static <T> PairIterator<T> withLast(Iterator<T> fromIterator) {
        return new PairIterator<T>() {
            T last = null;
            {
                if (fromIterator.hasNext())
                    last = fromIterator.next();
            }

            @Override
            public boolean hasNext() {
                return fromIterator.hasNext();
            }

            @Override
            public Pair<T> next() {
                T old = last;
                last = fromIterator.next();
                return new Pair<>(old, last);
            }
        };
    }
}
