package me.tuple.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IterableChain<T> implements SizeAwareIterative<T> {
	
	List<SizeAwareIterative<T>> chain = null;
	int size = 0;

	public IterableChain() {
		
	}
	
	public IterableChain<T> chain(Collection<T> collection) {
		return chain(new CollectionWraper<T>(collection));
	}
	
	public IterableChain<T> chain(SizeAwareIterative<T> iterative) {
		if (iterative.size()!=0) {
			if (chain==null) chain = new ArrayList<>();
			size += iterative.size();
			chain.add(iterative);
		}
		return this;
	}
	
	public IterableChain<T> chainAll(Iterable<SizeAwareIterative<T>> iteratives) {
		for (SizeAwareIterative<T> iterative: iteratives) {
			if (iterative.size()!=0) {
				if (chain==null) chain = new ArrayList<>();
				size += iterative.size();
				chain.add(iterative);
			}
		}
		return this;
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorChain();
	}

	@Override
	public int size() {
		return size;
	}

	
	class IteratorChain implements Iterator<T> {
		int index;
		Iterator<T> current;
		IteratorChain() {
			index=0;
			current=(chain==null)?new EmptyIterator<T>():chain.get(0).iterator();
		}

		@Override
		public boolean hasNext() {
			if (chain!=null && index<chain.size()) {
				if (current.hasNext()) return true;
				index++;
				if (index<chain.size()) {
					current = chain.get(index).iterator();
					return hasNext();
				}
			}
			return false;
		}

		@Override
		public T next() {
			if (hasNext()) return current.next();
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			current.remove();
		}
		
	}
	
	static class CollectionWraper<T> implements SizeAwareIterative<T> {
		
		Collection<T> collection;
		CollectionWraper(Collection<T> collection) {
			this.collection = collection;
		}

		@Override
		public int size() {
			return collection.size();
		}

		@Override
		public Iterator<T> iterator() {
			return collection.iterator();
		}
		
	}
}

