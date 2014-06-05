package me.tuple.util;

import java.util.Iterator;

public class SingleIterator<E> implements Iterator<E> {
	
	E e;
	public SingleIterator(E e) {
		this.e = e;
	}

	@Override
	public boolean hasNext() {
		return e!=null;
	}

	@Override
	public E next() {
		E tE = e;
		e = null;
		return tE;
	}

	@Override
	public void remove() {
			
	}

}






