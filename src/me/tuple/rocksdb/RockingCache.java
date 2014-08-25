package me.tuple.rocksdb;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tuple.util.DynamicByteBuffer;
import me.tuple.util.ExecutorServices;
import me.tuple.util.MemoryPrinter;
import me.tuple.util.Percentage;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class RockingCache {

	protected static Logger log = Logger.getLogger(RockingCache.class.getName());

	protected final File _folder;
	protected RocksDB _rDB;
	public final String name;		// for debug
	
	protected RockingCache(File folder) {
		this(folder, null);
	}
	protected RockingCache(File folder, Options options) {
		_folder = folder;
		_folder.mkdirs();
		
		if (options==null) {
			options = new Options()
			.setCreateIfMissing(true)
			.setMaxOpenFiles(-1)
			//.setAllowMmapReads(true)		// use true for SSD, else false
			//.setAllowMmapWrites(true)		// use true for SSD, else false
			.setMaxWriteBufferNumber(3)
			//.setTargetFileSizeBase(1024*1024*8)
			//.setKeepLogFileNum(2)
			//.setTargetFileSizeMultiplier(2)
			//.setMaxBytesForLevelBase(1024*1024*10)
			//.setMaxBytesForLevelMultiplier(10)
			//.setLevelZeroFileNumCompactionTrigger(2)
			;
		}
		
		try {
			_rDB = RocksDB.open(options, _folder.getAbsolutePath());
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't create", e);
			throw new RuntimeException(e);
		}
		
		name = _folder.getName();
	}
	
	protected RockingCache(RocksDB rDB, String name) {
		_folder = null;
		_rDB = rDB;
		this.name = name;
	}
	
	
	public void put(RockingObject ro) {
		put(ro.keyBytes(), ro.valueBytes());
	}
	
	//int putCount=0;
	public void put(byte key[], byte value[]) {
		try {
			_rDB.put(key, value);
			/*if (((++putCount)%1024)==0) {
				log.log(Level.WARNING, "{0} put count: {1}", new Object[]{name, putCount});
			}*/
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't put", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * If use one of putAsync(...) functions, you should not
	 * change to use put(...) functions. If you want to use
	 * put(...) functions, use it after calling waitAsync().
	 * @param ro
	 */
	public void putAsync(RockingObject ro) {
		AsyncRO aro = new AsyncRO(ro);
		this.putAsync(aro);
	}
	
	public void putAsync(Collection<? extends RockingObject> ros) {
		if (ros.size()==0) return;
		AsyncROs aros = new AsyncROs(ros);
		this.putAsync(aros);
	}
	
	public void putAsync(byte key[], byte value[]) {
		AsyncKbVb akv = new AsyncKbVb(key, value);
		this.putAsync(akv);
	}
	
	public byte[] get(byte key[]) {
		try {
			return _rDB.get(key);
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't load data", e);
			throw new RuntimeException(e);
		}
	}
	
	public byte[] get(long longId) {
		DynamicByteBuffer buff = new DynamicByteBuffer(8);
		buff.putVarLong(longId);
		byte kb[] = buff.toBytesBeforeCurrentPosition();
		buff.releaseForReuse();
		return get(kb);
	}
	
	public <T extends RockingObject> T get(long longId, Class<T> cla) {
		DynamicByteBuffer buff = new DynamicByteBuffer(8);
		buff.putVarLong(longId);
		byte kb[] = buff.toBytesBeforeCurrentPosition();
		buff.releaseForReuse();
		return get(kb, cla);
	}
	
	public <T extends RockingObject> T get(byte key[], Class<T> cla) {
		byte value[]=null;
		try {
			value = _rDB.get(key);
			return value==null?null:cla.getConstructor(byte[].class, byte[].class).newInstance(key, value);
		} catch (RocksDBException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.log(Level.WARNING, "RocksDB can't load data ("+cla+")", e);
			
			MemoryPrinter.printMemory(log, Level.WARNING, "key", key, 0, -1, 0);
			MemoryPrinter.printMemory(log, Level.WARNING, "value", value, 0, -1, 0);
			
			throw new RuntimeException(e);
		}
	}
	
	public <T extends RockingObject> Iterator<T> iterator(final Class<T> cla) {
		return iterator(cla, null);
	}
	
	public <T extends RockingObject> Iterator<T> iterator(final Class<T> cla, long first) {
		DynamicByteBuffer buff = new DynamicByteBuffer(8);
		buff.putVarLong(first);
		byte kb[] = buff.toBytesBeforeCurrentPosition();
		buff.releaseForReuse();
		return iterator(cla, kb);
	}
	
	public <T extends RockingObject> Iterator<T> iterator(final Class<T> cla, byte first[]) {
		final org.rocksdb.RocksIterator roIter = _rDB.newIterator();
		
		if (first==null) roIter.seekToFirst();
		else roIter.seek(first);
		
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return roIter.isValid();
			}

			@Override
			public T next() {
				byte ks[] = roIter.key();
				byte vs[] = roIter.value();
				T t;
				try {
					t = cla.getConstructor(byte[].class, byte[].class).newInstance(ks, vs);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					log.log(Level.WARNING, "RocksDB can't load data ("+cla+")", e);
					
					MemoryPrinter.printMemory(log, Level.WARNING, "key", ks, 0, -1, 0);
					MemoryPrinter.printMemory(log, Level.WARNING, "value", vs, 0, -1, 0);
					
					throw new RuntimeException(e);
				}
				
				roIter.next();
				
				return t;
			}

			@Override
			public void remove() {
				throw new java.lang.UnsupportedOperationException("Not support");
			}
			
		};
	}
	
	
	public boolean checkVersion(byte hashCode[]) {
		byte hashKey[] = "_._version_hash_code_._".getBytes();
		byte value[] = get(hashKey);
		
		
		if (value==null) {
			put(hashKey, hashCode);
			return true;
		} else {
			MemoryPrinter.printMemory(log, Level.INFO, this+" hash in db:", value, 0, value.length, 0);
			MemoryPrinter.printMemory(log, Level.INFO, this+" hash for check:", hashCode, 0, hashCode.length, 0);
			
			return Arrays.equals(hashCode, value);
		}
	}
	
	public void dispose() {
		// dispose rDB if and only if it was created by self.
		if (_folder!=null) {
			if (_rDB!=null) {
				_rDB.dispose();
				_rDB = null;
			}
		}
	}

	@Override
	protected void finalize() {
		dispose();
	}
	
	/**
	 * Wait for all async data write to database completed.
	 */
	public void waitAsync() {
		synchronized(this) {
			while (asyncT!=null) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void putAsync(Async a) {
		synchronized(this) {
			if (asyncT==null) {
				asyncPercentage = new Percentage(a.size(),0);
				asyncList = new ArrayList<Async>();
				asyncT = a;
				ExecutorServices.es(ExecutorServices.DB_NAME).execute(a);
				//new Thread(asyncT).start();
			} else {
				asyncPercentage.addTotal(a.size());
				asyncList.add(a);
			}
		}
	}
	
	public long uncompletedAsyncData() {
		synchronized(this) {
			return asyncPercentage==null?0:(long)asyncPercentage.gap();
		}
	}
	
	//private Async asyncT = null;
	private Async asyncT = null;
	private List<Async> asyncList = null;
	private Percentage asyncPercentage = null;
	protected abstract class Async implements Runnable {
		
		@Override
		public void run() {
			Async T = this;
			
			try {
				while(true) {
					int size = T.size();
					T.process();
					String perText;
					
					/*sleepCount+=size;
					if (sleepCount>=1024) {
						// used to avoid crashing (rocksdb, assert fail)
						sleepCount = 0;
						Thread.sleep(10);
					}*/
					
					synchronized(RockingCache.this) {
						asyncPercentage.addValue(size);
						perText = asyncPercentage.changedString();
						if (asyncList.size()==0) break;
						T = asyncList.remove(0);
					}
					
					if (perText!=null) {
						log.log(Level.INFO, "rDB({0}) saving {1}", new Object[]{name, perText});
					}
					
				}
			} catch(Exception e) {
				log.log(Level.WARNING, "rDB exception", e);
			} finally {
				synchronized(RockingCache.this) {
					if (asyncList.size()==0) {
						asyncT = null;
						asyncList = null;
						RockingCache.this.notifyAll();
						log.log(Level.INFO, "rDB({0}) saved 100%", new Object[]{name});
					} else {
						asyncT = asyncList.remove(0);
						ExecutorServices.es(ExecutorServices.DB_NAME).execute(asyncT);
					}
				}
			}
		}
		
		abstract protected void process();
		abstract protected int size();
	}
	
	protected class AsyncRO extends Async {
		final RockingObject ro;
		AsyncRO(RockingObject ro) {
			this.ro = ro;
		}
		@Override
		protected void process() {
			RockingCache.this.put(ro);
		}
		@Override
		protected int size() {
			return 1;
		}
	}
	
	protected class AsyncROs extends Async {
		final Collection<? extends RockingObject> ros;
		AsyncROs(Collection<? extends RockingObject> ros) {
			this.ros = ros;
		}
		@Override
		protected void process() {
			//int count=0;
			for (RockingObject ro: ros) {
				RockingCache.this.put(ro);
				//count++;
				/*try {
					Thread.sleep(0, 100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				//if (count>)
			}
		}
		@Override
		protected int size() {
			return ros.size();
		}
	}
	
	protected class AsyncKbVb extends Async {
		final byte key[];
		final byte value[];
		AsyncKbVb(byte key[], byte value[]) {
			this.key = key;
			this.value = value;
		}
		@Override
		protected void process() {
			RockingCache.this.put(key, value);
		}
		@Override
		protected int size() {
			return 1;
		}
	}

}





