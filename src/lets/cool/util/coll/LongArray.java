package lets.cool.util.coll;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A view of long[] and supports List interface.
 */
public class LongArray implements List<Long> {

    final long[] array;
    final int fromIndex/*inclusive*/, toIndex/*exclusive*/;

    public LongArray(long[] array) {
        this(array, 0, array.length);
    }

    /**
     *
     * @param array
     * @param fromIndex inclusive
     * @param toIndex exclusive
     */
    public LongArray(long[] array, int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= array.length || toIndex < 0 || toIndex > array.length || fromIndex > toIndex)
            throw new IndexOutOfBoundsException();

        this.array = array;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    @Override
    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    public boolean isEmpty() {
        return toIndex - fromIndex <= 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Long) {
            long l = (Long)o;
            return IntStream.range(fromIndex, toIndex).parallel().anyMatch(i -> array[i] == l);
            /*for (int i = fromIndex; i < toIndex; i++) {
                if (array[i] == l) return true;
            }*/
        }
        return false;
    }

    @Override
    public Iterator<Long> iterator() {
        return new Iterator<Long>() {
            int idx = fromIndex;
            @Override
            public boolean hasNext() {
                return idx < toIndex;
            }

            @Override
            public Long next() {
                return array[idx++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        return IntStream.range(fromIndex, toIndex).mapToObj(i -> array[i]).toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // TODO: optimize
        int size = size();

        Object[] longArray = toArray();

        if (a.length < size)
            return Arrays.copyOf(longArray, size, (Class<? extends T[]>) a.getClass());

        System.arraycopy(longArray, 0, a, 0, size);

        if (a.length > size) a[size] = null;

        return a;
    }

    @Override
    public boolean add(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().parallel().allMatch(this::contains);
    }

    @Override
    public boolean addAll(Collection<? extends Long> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Long> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long get(int index) {
        return array[fromIndex+index];
    }

    @Override
    public Long set(int index, Long element) {
        Long old = array[fromIndex+index];
        array[fromIndex+index] = element;
        return old;
    }

    @Override
    public void add(int index, Long element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if (o instanceof Long) {
            long l = (Long)o;
            for (int i = fromIndex; i < toIndex; i++) {
                if (array[i] == l) {
                    return i - fromIndex;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o instanceof Long) {
            long l = (Long)o;
            for (int i = toIndex-1; i >= toIndex; i--) {
                if (array[i] == l) {
                    return i - fromIndex;
                }
            }
        }
        return -1;
    }

    @Override
    public ListIterator<Long> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Long> listIterator(int index) {
        return new ListIterator<Long>() {
            int idx = index + fromIndex;
            int updatingIdx = idx;

            @Override
            public boolean hasNext() {
                return idx < toIndex;
            }

            @Override
            public Long next() {
                updatingIdx = idx++;
                return array[updatingIdx];
            }

            @Override
            public boolean hasPrevious() {
                return idx > fromIndex;
            }

            @Override
            public Long previous() {
                updatingIdx = --idx;
                return array[updatingIdx];
            }

            @Override
            public int nextIndex() {
                return idx;
            }

            @Override
            public int previousIndex() {
                return idx-1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(Long aLong) {
                array[updatingIdx] = aLong;
            }

            @Override
            public void add(Long aLong) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public List<Long> subList(int fromIndex, int toIndex) {
        return new LongArray(array, this.fromIndex+fromIndex, this.fromIndex+toIndex);
    }
}
