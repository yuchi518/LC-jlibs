package lets.cool.util.iter;

import java.util.Collection;
import java.util.Iterator;

public interface CountingIterator<T> extends SizeAwareIterator<T>, IndexAwareIterator<T>, ProgressAwareIterator<T> {

    /**
     * A basic implementation, it wraps an iterator.
     * @param <T>
     */
    class BasicImpl<T> implements CountingIterator<T> {
        protected int index;
        protected int size;
        protected Iterator<T> iterator;

        public BasicImpl(Collection<T> coll) {
            this(coll.size(), coll.iterator());
        }

        public BasicImpl(int size, Iterator<T> iterator) {
            this.index = 0;
            this.size = size;
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.size!=0 && iterator.hasNext();
        }

        @Override
        public T next() {
            index++;
            return iterator.next();
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public int index() {
            return this.index;
        }

        @Override
        public double progressPercentage() {
            return this.size==0?0:(this.index*100.0/this.size);
        }
    }

    /**
     *
     * Example:
     * Phase 0: 50 steps  ==> 0%, 1% ... 50%
     * Phase 1: 25 steps  ==> 50%, 52%, ... 100%
     *
     * @param <T>
     */
    abstract class PhaseImpl<T> implements CountingIterator<T> {

        final protected int sizeOfPhases;
        protected int indexOfPhases;
        protected int sizeOfSteps;
        protected int indexOfSteps;

        public PhaseImpl(int sizeOfPhases) {
            this.sizeOfPhases = sizeOfPhases;
            this.indexOfPhases = 0;
            this.sizeOfSteps = 100;
            this.indexOfSteps = 0;
        }

        /**
         * Call this before return next()
         * @return If true, go to next phase. Use for verification.
         */
        protected boolean nextStep() {
            if (++indexOfSteps >= this.sizeOfSteps) {
                this.indexOfPhases++;
                this.indexOfSteps = 0;
                return true;
            }
            return false;
        }

        /**
         * If size of steps is different to 100, change it before call nextStep() first time.
         * @param size
         */
        protected void changeSizeOfSteps(int size) {
            this.sizeOfSteps = size;
        }

        @Override
        public int index() {
            return (int)progressPercentage();
        }

        @Override
        public double progressPercentage() {
            return 0;
        }

        @Override
        public int size() {
            return 100;
        }
    }
}
