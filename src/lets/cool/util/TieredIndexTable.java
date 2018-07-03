package lets.cool.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.javaewah.EWAHCompressedBitmap;
import com.googlecode.javaewah.EWAHIterator;
import com.googlecode.javaewah.RunningLengthWord;

/**
 * Note:
 * 1. Not Thread Safe, be careful to use it.
 * 2. Iterate the table is not order guarantee.
 * @author YuChi
 *
 * @param <E>
 */
public class TieredIndexTable<E> {

	final protected static Logger log = Logger.getLogger(TieredIndexTable.class.getName());
	
	final protected Comparator<? super E> _comparator;
	final protected int _numberOfTiers;
	final protected int _tierBits[];
	final protected Map<?,?> _rootMap;
	
	public TieredIndexTable(int numberOfTiers, int numberOfBitsInFirstTier, Comparator<? super E> comparator) {
		_comparator = comparator;
		_numberOfTiers = numberOfTiers;
		//_tierBits = tierBits;
		
		if (numberOfTiers <= 0) {
			throw new OutOfBoundsException("numberOfTiers <= 0");
		} else if (numberOfBitsInFirstTier <= 0) {
			throw new OutOfBoundsException("tierBits <= 0");
		} else if (numberOfBitsInFirstTier >= 32) {
			throw new OutOfBoundsException("tierBits >= 32");
		} else {
			//
			if (((numberOfBitsInFirstTier + (numberOfTiers-1)) + 1) * (numberOfBitsInFirstTier + (numberOfTiers-1)) / 2 >= 64) {
				throw new OutOfBoundsException("((numberOfBitsInFirstTier + (numberOfTiers-1)) + 1) * (numberOfBitsInFirstTier + (numberOfTiers-1)) / 2 >= 64");
			}
		}
		
		if (numberOfTiers==1) {
			_rootMap = new HashMap<Number, Set<E>>();
		} else {
			_rootMap = new HashMap<Number, Map<?,?>>();
		}
		
		_tierBits = new int[numberOfTiers];
		for (int i=0; i<numberOfTiers; i++) {
			_tierBits[i] = numberOfBitsInFirstTier + i;	//* (int)Math.pow(2, i);
		}
	}
	
	public void printIndexTable(String title)
	{
		//System.out.println(_rootMap);
		StringBuffer output = new StringBuffer();
		printMap(_rootMap, 0, output);
		
		log.log(Level.INFO, "<====== {0} \n{1}\n ======>", new Object[]{title, output});
		//System.out.println(output);
	}
	
	void printMap(Map<?,?> map, int deep, StringBuffer output)
	{
		StringBuffer tab = new StringBuffer();
		for (int i=0; i<deep; i++) tab.append('\t');
		
		for (Map.Entry<?,?> entry: map.entrySet())
		{
			if (deep==(_numberOfTiers-1)) {
				output.append("" + tab + "[" + entry.getKey() + "]: " + entry.getValue() + "\n");
			//} else if (deep==(_numberOfTiers-2)) {
			} else {
				output.append("" + tab + "[" + entry.getKey() + "]\n");
				printMap((Map<?, ?>)entry.getValue(), deep+1, output);
			}
		}
	}
	
	public void addObject(EWAHCompressedBitmap bitmap, E e)
	{
		Set<E> set = getLastSet(bitmap, true);
		set.remove(e);		// remove old one first, else old one will not be replaced by new one
		set.add(e);
	}
	
	public void removeObject(EWAHCompressedBitmap bitmap, E e)
	{
		Set<E> set = getLastSet(bitmap, false);
		if (set!=null) set.remove(e);
	}
	
	@SuppressWarnings("unchecked")
	protected Set<E> getLastSet(EWAHCompressedBitmap bitmap, boolean created)
	{
		long[] indexes = getIndexesFromBitmap(bitmap);
		Map<?,?> map = _rootMap;
		
		int i=0;
		while (true)
		{
			if (i==(_numberOfTiers-1)) {
				// is me
				Set<E> set = ((Map<Number, Set<E>>)map).get(indexes[i]);
				if (set==null && created) {
					set = _comparator==null?new TreeSet<E>():new TreeSet<E>(_comparator);
					((Map<Number, Set<E>>)map).put(indexes[i], set);
				}
				return set;
			} else if (i==(_numberOfTiers-2)) {
				// last one
				Map<Number, Set<E>> nextMap = (Map<Number, Set<E>>)map.get(indexes[i]);
				if (nextMap==null) {
					if (created) {
						// put an new one
						nextMap = new HashMap<Number, Set<E>>();
						((Map<Number, Map<?,?>>)map).put(indexes[i], nextMap);
					} else
						return null;
				}
				map = nextMap;
			} else {
				//
				Map<Number, Map<?,?>> nextMap = (Map<Number, Map<?,?>>)map.get(indexes[i]);
				if (nextMap==null) {
					if (created) {
						nextMap = new HashMap<Number, Map<?,?>>();
						((Map<Number, Map<?,?>>)map).put(indexes[i], nextMap);
					} else
						return null;
				}
				map = nextMap;
			}
			i++;
		}
	}
	
	public void interateObjects(EWAHCompressedBitmap bitmap, FindingHandler<E> handler)
	{
		long[] indexes = getIndexesFromBitmap(bitmap);
		iteratorMap(this._rootMap, 0, indexes, bitmap, handler);
	}
	
	private void iteratorMap(Map<?,?> map, int deep, long[] indexes, EWAHCompressedBitmap bitmap, FindingHandler<E> handler)
	{
		for (Map.Entry<?,?> entry: map.entrySet())
		{
			if (deep==(_numberOfTiers-1)) {
				if ((((Number)entry.getKey()).longValue() & indexes[deep])==indexes[deep]) {
					@SuppressWarnings("unchecked")
					Set<E> set = (Set<E>)(entry.getValue());
					Iterator<E> iter = set.iterator();
					while(iter.hasNext()) {
						E e = iter.next();
						if (handler.foundObject(e)) {
							iter.remove();
						}
						
						if (handler.shouldStop()) return;
					}
				}
			//} else if (deep==(_numberOfTiers-2)) {
			} else {
				if ((((Number)entry.getKey()).longValue() & indexes[deep])==indexes[deep])		// contains subset
					iteratorMap((Map<?, ?>)entry.getValue(), deep+1, indexes, bitmap, handler);
			}
		}
	}
	
	
	protected long[] getIndexesFromBitmap(EWAHCompressedBitmap bitmap)
	{
		//final ArrayList<Integer> v = new ArrayList<Integer>();
	    final EWAHIterator iter = bitmap.getEWAHIterator();
	    int pos = 0;
	    final int sizeInBits = bitmap.sizeInBits();
	    long []_indexes = new long[_numberOfTiers];
	    while (iter.hasNext())
	    {
	    	RunningLengthWord localrlw = iter.next();
	    	if (localrlw.getRunningBit()) 
	    	{
	    		for (int j = 0; j < localrlw.getRunningLength(); ++j)
		        {
	    			for (int c = 0; c < EWAHCompressedBitmap.WORD_IN_BITS; ++c) {
	    				//v.add(new Integer(pos++));
	    				if (pos < sizeInBits) {
	    					//v|=(pos)%64;
	    					cb(pos, _indexes);
	    				}
	    				pos++;
	    			}
		        }
	    	} else {
	    		pos += EWAHCompressedBitmap.WORD_IN_BITS * localrlw.getRunningLength();
	    	}
	    	for (int j = 0; j < localrlw.getNumberOfLiteralWords(); ++j) {
	    		long data = iter.buffer()[iter.literalWords() + j];
	    		while (data != 0) {
	    			final int ntz = Long.numberOfTrailingZeros(data);
	    			data ^= (1l << ntz);
	    			//v.add(new Integer(ntz + pos));
	    			if ((ntz+pos) < sizeInBits) {
	    				//v|=(ntz+pos)%64;
	    				cb((ntz+pos), _indexes);
	    			}
	    		}
	    		pos += EWAHCompressedBitmap.WORD_IN_BITS;
	    	}
	    }
	    
	    return _indexes;
	    /*Number num[] = new Number[_numberOfTiers];
	    for (int i=0; i<_numberOfTiers; i++) {
	    	if (_indexes[i] > (long)Integer.MAX_VALUE || _indexes[i] < (long)Integer.MIN_VALUE) {
	    		num[i] = (Long)_indexes[i]; 
	    	} else {
	    		num[i] = (Integer)(int)_indexes[i];
	    	}
	    }
	    
	    return num;*/
	}
	
	protected void cb(int v, long []_indexes)
	{
		//int bitPos[]=new int[_numberOfTiers];
		final boolean ver = false;
		for (int i=0; i<_numberOfTiers; i++)
		{
			if (ver) {
				// 
				long mv = v % ((_tierBits[i]+1)*_tierBits[i]/2);
				for (int l=_tierBits[i]; l>0; l--)
				{
					//System.out.printf("v:%d l:%d\n", mv, l);
					if (mv<l) {
						//bitPos[i] = v;
						if (_indexes!=null) _indexes[i] |= (1L<<(mv));
						break;
					} else {
						mv -= l;
					}
				}
			} else {
				long mv = v % _tierBits[i];
				//bitPos[i] = v;
				if (_indexes!=null) _indexes[i] |= (1L<<(mv));
			}
			
		}
		
		//return bitPos;
	}
	
	public static interface FindingHandler<E>
	{
		public boolean foundObject(E e);		// return true to delete object
		public boolean shouldStop();			// return true for stop iterator. Call for each time after call foundObject
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










