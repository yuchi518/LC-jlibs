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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IterableChain<T> implements CountingIterable<T> {
	
	List<SizeAwareIterable<T>> chains;

	/**
	 * Total size, not chains' size
	 */
	int totalSize = 0;

	public IterableChain() {
		chains = new ArrayList<>();
	}
	
	public IterableChain<T> chain(Collection<T> collection) {
		return chain(new CollectionWrapper<>(collection));
	}
	
	public IterableChain<T> chain(SizeAwareIterable<T> iterative) {
		if (iterative.size()!=0) {
			totalSize += iterative.size();
			chains.add(iterative);
		}
		return this;
	}
	
	public IterableChain<T> chainAll(Iterable<SizeAwareIterable<T>> iteratives) {
		for (SizeAwareIterable<T> iterative: iteratives) {
			if (iterative.size()!=0) {
				totalSize += iterative.size();
				chains.add(iterative);
			}
		}
		return this;
	}

	@Override
	public CountingIterator<T> iterator() {
		return new IteratorChain();
	}

	@Override
	public int size() {
		return totalSize;
	}

	
	class IteratorChain implements CountingIterator<T> {

	    int index;
		int indexOfChains;
		Iterator<T> current;

		IteratorChain() {
		    index = 0;
			indexOfChains = 0;
			current = (chains == null || chains.size() == 0) ? new EmptyIterator<>() : chains.get(0).iterator();
		}

		@Override
		public boolean hasNext() {
			if (chains != null) {
                if (indexOfChains < chains.size()) {
                    if (current.hasNext()) return true;
                    indexOfChains++;
                    if (indexOfChains < chains.size()) {
                        current = chains.get(indexOfChains).iterator();
                        return hasNext();
                    }
                }
            }
			return false;
		}

		@Override
		public T next() {
			if (hasNext()) {
			    index++;
			    return current.next();
            }
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			current.remove();
		}

		@Override
		public int index() {
			return index;
		}

		@Override
		public double progressPercentage() {
			return totalSize==0 ? 100 : (index * 100.0 / totalSize);
		}

		@Override
		public int size() {
			return totalSize;
		}
	}
	
	static class CollectionWrapper<T> implements SizeAwareIterable<T> {
		
		Collection<T> collection;
		CollectionWrapper(Collection<T> collection) {
			this.collection = collection;
		}

		@Override
		public int size() {
			return collection.size();
		}

		@Override
		public SizeAwareIterator<T> iterator() {
			return new CountingIterator.BasicImpl<>(collection);
		}
		
	}
}

