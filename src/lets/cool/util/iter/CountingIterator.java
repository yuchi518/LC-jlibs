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

package lets.cool.util.iter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    class ReverseImpl<T> implements CountingIterator<T> {
        protected int index;
        protected int size;
        protected ListIterator<T> reverseIterator;

        public ReverseImpl(List<T> list) {
            this(list.size(), list.listIterator(list.size()));
        }

        public ReverseImpl(int size, ListIterator<T> reverseIterator) {
            this.index = 0;
            this.size = size;
            this.reverseIterator = reverseIterator;
        }

        @Override
        public boolean hasNext() {
            return this.size!=0 && reverseIterator.hasPrevious();
        }

        @Override
        public T next() {
            index++;
            return reverseIterator.previous();
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
            //System.err.printf("Phase:%d/%d, Step:%d/%d\n", indexOfPhases, sizeOfPhases, indexOfSteps, sizeOfSteps);
            if (++indexOfSteps >= this.sizeOfSteps) {
                this.indexOfPhases++;

                //if (indexOfPhases >= 8 ) System.exit(111);
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
            if (indexOfPhases >= sizeOfPhases) return 100;
            return (100.0*indexOfPhases/sizeOfPhases) + (sizeOfSteps==0?0:(100.0*indexOfSteps/sizeOfPhases/sizeOfSteps));
        }

        @Override
        public int size() {
            return 100;
        }
    }
}
