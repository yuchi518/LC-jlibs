package lets.cool.util.iter;

import java.util.Iterator;

public interface ShrinkIterator<IN, OUT> extends Iterator<OUT> {

    abstract class Impl<IN, OUT> implements ShrinkIterator<IN, OUT> {
        final protected Iterator<IN> inputIterator;

        public Impl(Iterator<IN> iterator) {
            this.inputIterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return inputIterator.hasNext();
        }

        @Override
        public OUT next() {
            OUT out;
            do {
                IN in = inputIterator.next();
                out = process(in);
            } while (out == null);
            return out;
        }

        abstract public OUT process(IN inputData);
    }
}
