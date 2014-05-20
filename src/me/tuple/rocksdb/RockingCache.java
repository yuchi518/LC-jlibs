package me.tuple.rocksdb;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tuple.util.MemoryPrinter;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class RockingCache {

	protected static Logger log = Logger.getLogger(RockingCache.class.getName());

	protected final File _folder;
	protected final RocksDB _rDB;

	protected RockingCache(File folder) {
		this(folder, null);
	}
	protected RockingCache(File folder, Options options) {
		_folder = folder;
		_folder.mkdirs();
		
		if (options==null) {
			options = new Options();
			options.setCreateIfMissing(true);
			options.setMaxOpenFiles(-1);
			options.setAllowMmapReads(false);		// use true for SSD, else false
			options.setAllowMmapWrites(false);		// use true for SSD, else false
			options.setMaxWriteBufferNumber(4);
		}
		
		try {
			_rDB = RocksDB.open(options, _folder.getAbsolutePath());
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't create", e);
			throw new RuntimeException(e);
		}
	}
	
	protected RockingCache(RocksDB rDB) {
		_folder = null;
		_rDB = rDB;
	}
	
	public void put(RockingObject ro) {
		put(ro.keyBytes(), ro.valueBytes());
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
		AsyncROs aros = new AsyncROs(ros);
		this.putAsync(aros);
	}
	
	public void put(byte key[], byte value[]) {
		try {
			_rDB.put(key, value);
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't put", e);
			throw new RuntimeException(e);
		}
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
	
	
	public <T extends RockingObject> T get(byte key[], Class<T> cla) {
		try {
			byte value[] = _rDB.get(key);
			return value==null?null:cla.getConstructor(byte[].class, byte[].class).newInstance(key, value);
		} catch (RocksDBException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.log(Level.WARNING, "RocksDB can't load data", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void finalize() {
		_rDB.close();
	}
	
	/**
	 * Wait for all async data write to database completed.
	 */
	public void waitAsync() {
		synchronized(this) {
			if (asyncT!=null) {
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
				asyncT = a;
				asyncList = new ArrayList<Async>();
				new Thread(asyncT).start();
			} else {
				asyncList.add(a);
			}
		}
	}
	
	
	private Async asyncT = null;
	private List<Async> asyncList = null;
	protected abstract class Async implements Runnable {

		@Override
		public void run() {
			Async T = this;
			
			while(true) {
				T.process();
				
				synchronized(RockingCache.this) {
					if (asyncList.size()==0) {
						asyncT = null;
						asyncList = null;
						RockingCache.this.notifyAll();
						return;
					} else {
						T = asyncList.remove(0);
					}
				}
				
			}
		}
		
		abstract protected void process();
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
		
	}
	
	protected class AsyncROs extends Async {
		final Collection<? extends RockingObject> ros;
		AsyncROs(Collection<? extends RockingObject> ros) {
			this.ros = ros;
		}
		@Override
		protected void process() {
			for (RockingObject ro: ros) {
				RockingCache.this.put(ro);
			}
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
		
	}

}





