package lets.cool.util.iter;

public interface ShrinkIterable<IN, OUT> extends Iterable<OUT> {
    @Override
    ShrinkIterator<IN, OUT> iterator();

    abstract class Impl<IN, OUT> implements ShrinkIterable<IN, OUT> {

        final protected Iterable<IN> inputIterable;

        public Impl(Iterable<IN> iterator) {
            this.inputIterable = iterator;
        }

        abstract public OUT process(IN inputData);

        @Override
        public ShrinkIterator<IN, OUT> iterator() {
            return new ShrinkIterator.Impl<>(inputIterable.iterator()) {
                @Override
                public OUT process(IN inputData) {
                    return ShrinkIterable.Impl.this.process(inputData);
                }
            };
        }
    }
}
