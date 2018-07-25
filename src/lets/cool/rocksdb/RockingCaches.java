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

package lets.cool.rocksdb;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * Thread safe.
 * @author YuChi
 *
 */
public class RockingCaches {

	protected static Logger log = Logger.getLogger(RockingCaches.class.getName());
	
	protected final File _folder;
	protected final Map<String, RocksDB> _rDBs;
	protected final Options _options;
	
	public RockingCaches(File folder) {
		this(folder, null);
	}
	public RockingCaches(File folder, Options options) {
		_folder = folder;
		_folder.mkdirs();
		_rDBs = new HashMap<>();
		if (options==null) {
			_options = new Options()
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
		} else {
			_options = options;
		}
	}
	
	public RocksDB db(String name) {
		return db(name, null);
	}
	public RocksDB db(String name, Options options) {
		RocksDB rDB = _rDBs.get(name);
		if (rDB!=null) return rDB;
		
		synchronized(_rDBs) {
			// try again
			rDB = _rDBs.get(name);
			if (rDB!=null) return rDB;
			
			File folder = new File(_folder, name);
			folder.mkdirs();
			
			try {
				rDB = RocksDB.open(options==null?_options:options, folder.getAbsolutePath());
			} catch (RocksDBException e) {
				log.log(Level.WARNING, "RocksDB can't create", e);
				throw new RuntimeException(e);
			}
			
			_rDBs.put(name, rDB);
			
			return rDB;
		}
	}
	
	public RockingCache cache(String name) {
		return new RockingCache(db(name), name);
	}

    /**
     *
     * @param name The name of database
     * @param subClass The sub class for cache
     * @param <S>
     * @return
     */
	public <S extends RockingCache> S cache(String name, Class<S> subClass) {
		try {
			return subClass.getConstructor(RocksDB.class,String.class).newInstance(db(name),name);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.log(Level.WARNING, "Unsupported class", e);
		}
		return null;
	}

	public <T extends RockingObject> RockingObjectsCache<T> objectsCache(String name, Class<T> forClass) {
		return new RockingObjectsCache<>(forClass, db(name), name);
	}
	
	/**
	 * Return a RockingSetCache cache, make sure the DB is this type.
	 * @param name
	 * @return A RockingSetCache cache.
	 */
	public RockingSetCache setCache(String name) {
		return new RockingSetCache(db(name), name);
	}
	
	public RockingPropertiesCache propertiesCache(String name) {
		return new RockingPropertiesCache(db(name), name);
	}
	
	/**
	 * Use 'ro.getClass().getPackage().getName() + "." + ro.getClass().getName()' as cache folder name.
	 * @param ro
	 */
	public void put(RockingObject ro) {
		put(ro.getClass().getPackage().getName() + "." + ro.getClass().getName(), ro.keyBytes(), ro.valueBytes());
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
	
	/**
	 * Call this before application termination.
	 */
	public void dispose() {
		for (RocksDB rDB: _rDBs.values()) {
			rDB.close();
		}
		_rDBs.clear();
		_options.close();
	}

	@Override
	protected void finalize() {
		dispose();
	}

}




