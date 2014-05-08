package me.tuple.rocksdb;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class RockingCaches {

	protected static Logger log = Logger.getLogger(RockingCaches.class.getName());
	
	protected final File _folder;
	protected final Map<String, RocksDB> _rDBs;
	protected final Options _options;
	
	protected RockingCaches(File folder) {
		this(folder, null);
	}
	protected RockingCaches(File folder, Options options) {
		_folder = folder;
		_folder.mkdirs();
		_rDBs = new HashMap<String, RocksDB>();
		if (options==null) {
			_options = new Options();
			_options.setCreateIfMissing(true);
			_options.setMaxOpenFiles(-1);
			_options.setAllowMmapReads(true);
			_options.setAllowMmapWrites(true);
			_options.setMaxWriteBufferNumber(3);
		} else {
			_options = options;
		}
	}
	
	protected RocksDB db(String name) {
		RocksDB rDB = _rDBs.get(name);
		if (rDB!=null) return rDB;
		
		File folder = new File(_folder, name);
		folder.mkdirs();
		
		try {
			rDB = RocksDB.open(_options, _folder.getAbsolutePath());
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't create", e);
			throw new RuntimeException(e);
		}
		
		_rDBs.put(name, rDB);
		
		return rDB;
	}
	
	/**
	 * User ro.getClass().getName() as cache folder name.
	 * @param ro
	 */
	public void put(RockingObject ro) {
		put(ro.getClass().getName(), ro.keyBytes(), ro.valueBytes());
	}
	
	/**
	 * User name as cache folderName
	 * @param name
	 * @param ro
	 */
	public void put(String name, RockingObject ro) {
		put(name, ro.keyBytes(), ro.valueBytes());
	}
	
	public void put(String name, byte key[], byte value[]) {
		try {
			db(name).put(key, value);
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't put", e);
			throw new RuntimeException(e);
		}
	}
	
	public byte[] get(String name, byte key[]) {
		try {
			return db(name).get(key);
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't load data", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * User cla.getName as cache folder name.
	 * @param key
	 * @param cla
	 * @return
	 */
	public <T extends RockingObject> T get(byte key[], Class<T> cla) {
		return get(cla.getName(), key, cla);
	}
	
	public <T extends RockingObject> T get(String name, byte key[], Class<T> cla) {
		try {
			byte value[] = db(name).get(key);
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
		for (RocksDB rDB: _rDBs.values()) {
			rDB.close();
		}
		_rDBs.clear();
	}

}




