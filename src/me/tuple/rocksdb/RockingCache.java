package me.tuple.rocksdb;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tuple.util.MemoryPrinter;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public abstract class RockingCache {

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
			options.setAllowMmapReads(true);
			options.setAllowMmapWrites(true);
			options.setMaxWriteBufferNumber(3);
		}
		
		try {
			_rDB = RocksDB.open(options, _folder.getAbsolutePath());
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't create", e);
			throw new RuntimeException(e);
		}
	}
	
	public void put(RockingObject ro) {
		put(ro.keyBytes(), ro.valueBytes());
	}
	
	public void put(byte key[], byte value[]) {
		try {
			_rDB.put(key, value);
		} catch (RocksDBException e) {
			log.log(Level.WARNING, "RocksDB can't put", e);
			throw new RuntimeException(e);
		}
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

}





