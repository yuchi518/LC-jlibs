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
import java.util.function.Consumer;

import lets.cool.util.logging.Logr;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

/**
 * Thread safe.
 * @author YuChi
 *
 */
public class RockingCaches {

	final protected static Logr log = Logr.logger();
	
	protected final File _folder;
	protected final Map<String, RocksDB> _rDBs;
	protected final Options _options;
	
	public RockingCaches(File folder) {
		this(folder, null);
	}
	public RockingCaches(File folder, Consumer<Options> optionsConsumer) {
		_folder = folder;
		_folder.mkdirs();
		_rDBs = new HashMap<>();
		_options = RockingCache.defaultOptions();

		if (optionsConsumer!=null) {
			optionsConsumer.accept(_options);
		}
	}

	public RocksDB db(String name) {
		return db(name, null, false);
	}
	public RocksDB db(String name, boolean hardReadonly) {
		return db(name, null, hardReadonly);
	}
	public RocksDB dbRO(String name) {
		return db(name, null, true);
	}
	public RocksDB db(String name, Consumer<Options> optionsConsumer, boolean hardReadonly) {
		RocksDB rDB = _rDBs.get(name);
		if (rDB!=null) return rDB;

		Options options = new Options(_options);

		if (optionsConsumer!=null) {
			optionsConsumer.accept(options);
		}
		
		synchronized(_rDBs) {
			// try again
			rDB = _rDBs.get(name);
			if (rDB!=null) return rDB;
			
			File folder = locationOfDB(name);
			folder.mkdirs();
			
			try {
				rDB = hardReadonly ?
						RocksDB.openReadOnly(options, folder.getAbsolutePath()) :
						RocksDB.open(options, folder.getAbsolutePath());
			} catch (RocksDBException e) {
				log.warn("RocksDB can't create", e);
				throw new RuntimeException(e);
			}
			
			_rDBs.put(name, rDB);
			
			return rDB;
		}
	}

	final public File locationOfDB(String name) {
		return new File(_folder, name);
	}
	
	public RockingCache cache(String name) {
		return new RockingCache(db(name, false), name, false);
	}

	public RockingCache cacheRO(String name) {
		return new RockingCache(db(name, true), name, true);
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
			return subClass.getConstructor(RocksDB.class, String.class, boolean.class).newInstance(db(name, false), name, false);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.warn("Unsupported class", e);
		}
		return null;
	}

	public <S extends RockingCache> S cacheRO(String name, Class<S> subClass) {
		try {
			return subClass.getConstructor(RocksDB.class, String.class, boolean.class).newInstance(db(name, true), name, true);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.warn("Unsupported class", e);
		}
		return null;
	}

	public <TK extends RockingKey, TO extends RockingObject> RockingObjectsCache<TK, TO> objectsCache(String name, Class<TK> kcla, Class<TO> cla) {
		return new RockingObjectsCache<>(kcla, cla, db(name, false), name, false);
	}
	public <TK extends RockingKey, TO extends RockingObject> RockingObjectsCache<TK, TO> objectsCache(String name, Class<TK> kcla, Class<TO> cla, boolean hardReadonly) {
		return new RockingObjectsCache<>(kcla, cla, db(name, hardReadonly), name, hardReadonly);
	}
	public <TK extends RockingKey, TO extends RockingObject> RockingObjectsCache<TK, TO> objectsCacheRO(String name, Class<TK> kcla, Class<TO> cla) {
		return new RockingObjectsCache<>(kcla, cla, db(name, true), name, true);
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
	public RockingPropertiesCache propertiesCache(String name, boolean hardReadonly) {
		return new RockingPropertiesCache(db(name, hardReadonly), name, hardReadonly);
	}

	public RockingBlobsCache blobsCache(String name) {
		return new RockingBlobsCache(db(name), name);
	}
	public RockingBlobsCache blobsCache(String name, boolean hardReadonly) {
		return new RockingBlobsCache(db(name, hardReadonly), name, hardReadonly);
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




