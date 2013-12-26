package cc.waynes.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import com.googlecode.javaewah.EWAHCompressedBitmap;
import com.googlecode.javaewah.EWAHIterator;
import com.googlecode.javaewah.RunningLengthWord;

public class TieredIndexTable<E> {

	final protected Comparator<? super E> _comparator;
	final protected int _numberOfTiers;
	final protected int _tierBits[];
	final protected Map<?,?> _rootMap;
	
	public TieredIndexTable(int numberOfTiers, int tierBits, Comparator<? super E> comparator) {
		_comparator = comparator;
		_numberOfTiers = numberOfTiers;
		//_tierBits = tierBits;
		
		if (numberOfTiers <= 0) {
			throw new OutOfBoundsException("numberOfTiers <= 0");
		} else if (tierBits <= 0) {
			throw new OutOfBoundsException("tierBits <= 0");
		} else if (tierBits >= 32) {
			throw new OutOfBoundsException("tierBits >= 32");
		} else {
			//
			if (tierBits * Math.pow(2, numberOfTiers-1) >= 64) {
				throw new OutOfBoundsException("tierBits*(2^(numberOfTiers-1)) >= 64");
			}
		}
		
		if (numberOfTiers==1) {
			_rootMap = new java.util.HashMap<Long, java.util.Set<E>>();
		} else {
			_rootMap = new java.util.HashMap<Integer, Map<?,?>>();
		}
		
		_tierBits = new int[numberOfTiers];
		for (int i=0; i<numberOfTiers; i++) {
			_tierBits[i] = tierBits * (int)Math.pow(2, i);
		}
	}
	
	public void addObject(EWAHCompressedBitmap bitmap, E e)
	{
		//final ArrayList<Integer> v = new ArrayList<Integer>();
		long v=0;
	    final EWAHIterator i = bitmap.getEWAHIterator();
	    int pos = 0;
	    final int sizeInBits = bitmap.sizeInBits();
	    while (i.hasNext())
	    {
	    	RunningLengthWord localrlw = i.next();
	    	if (localrlw.getRunningBit()) 
	    	{
	    		for (int j = 0; j < localrlw.getRunningLength(); ++j)
		        {
	    			for (int c = 0; c < EWAHCompressedBitmap.wordinbits; ++c) {
	    				//v.add(new Integer(pos++));
	    				if (pos < sizeInBits) v|=(pos)%64;
	    				pos++;
	    			}
		        }
	    	} else {
	    		pos += EWAHCompressedBitmap.wordinbits * localrlw.getRunningLength();
	    	}
	    	for (int j = 0; j < localrlw.getNumberOfLiteralWords(); ++j) {
	    		long data = i.buffer()[i.literalWords() + j];
	    		while (data != 0) {
	    			final int ntz = Long.numberOfTrailingZeros(data);
	    			data ^= (1l << ntz);
	    			//v.add(new Integer(ntz + pos));
	    			if (((ntz+pos)%64) < sizeInBits) v|=(ntz+pos)%64;
	    		}
	    		pos += EWAHCompressedBitmap.wordinbits;
	    	}
	    }
	    while ((v.size() > 0) && (v.get(v.size() - 1).intValue() >= bitmap.sizeInBits()))
	      v.remove(v.size() - 1);
	    return v;
	}
	
	
	public static class OutOfBoundsException extends RuntimeException
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public OutOfBoundsException() 
		{
			super();
		}
		
		public OutOfBoundsException(String message)
		{
			super(message);
		}
		
		public OutOfBoundsException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}

}










