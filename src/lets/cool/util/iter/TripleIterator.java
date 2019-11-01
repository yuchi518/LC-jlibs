package lets.cool.util.iter;

import java.util.Collection;
import java.util.Iterator;

public interface TripleIterator<T> extends Iterator<TripleIterator.Triple<T>> {
    class Triple<T> {
        final public T a;
        final public T b;
        final public T c;
        public Triple(T a, T b, T c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    static <T> TripleIterator<T> withNeighbors(Collection<T> fromCollection) {
        return withNeighbors(fromCollection.iterator());
    }

    static <T> TripleIterator<T> withNeighbors(Iterator<T> fromIterator) {
        return new TripleIterator<T>() {
            T first = null;
            T last = null;
            T curr = null;
            {
                if (fromIterator.hasNext())
                    first = last = fromIterator.next();
                if (fromIterator.hasNext())
                    curr = fromIterator.next();
            }

            @Override
            public boolean hasNext() {
                return curr != null;
            }

            @Override
            public Triple<T> next() {
                Triple<T> o;

                if (fromIterator.hasNext()) {
                    T next = fromIterator.next();
                    o = new Triple<>(last, curr, next);
                    last = curr;
                    curr = next;
                } else {
                    o = new Triple<>(last, curr, first);
                    last = curr;
                    curr = null;
                }

                return o;
            }
        };
    }
}
