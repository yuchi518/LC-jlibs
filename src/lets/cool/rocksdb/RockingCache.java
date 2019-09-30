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
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

import lets.cool.util.*;
import lets.cool.util.logging.Level;
import lets.cool.util.logging.Logr;

import org.rocksdb.*;

public class RockingCache {

	final protected static Logr log = Logr.logger();

	public static Options defaultOptions() {
	    return new Options()
                .setCreateIfMissing(false)
                .setMaxOpenFiles(-1)
                //.setAllowMmapReads(true)		// use true for SSD, else false
                //.setAllowMmapWrites(true)		// use true for SSD, else false
                .setMaxWriteBufferNumber(3)
                //.setTargetFileSizeBase(1024*1024*8)
                //.setKeepLogFileNum(2)
                .setTargetFileSizeMultiplier(2)
                //.setMaxBytesForLevelBase(1024*1024*10)
                //.setMaxBytesForLevelMultiplier(10)
                //.setLevelZeroFileNumCompactionTrigger(2)
                .setCompressionType(CompressionType.SNAPPY_COMPRESSION)
                ;
    }

	final public String name;		// for debug
    final private File _folder;
	final private RocksDB _rDB;
	final private Options _options;
	final private Map<String, ColumnFamilyHandle> columnFamilyHandleMap = new HashMap<>();
	final private boolean _hardReadonly;
	private boolean _softReadonly;
	private WeakHashMap<RockingKey, WeakReference<RockingObject>> uniqueObjects;
	private int cacheHit = 0, cacheError = 0, cacheMiss = 0;

	protected RockingCache(File folder) {
		this(folder, null, false);
	}

	protected RockingCache(File folder, Consumer<Options> optionsConsumer, boolean hardReadonly) {
		_folder = folder;
		_folder.mkdirs();

		_options = defaultOptions();
		
		if (optionsConsumer!=null) {
			optionsConsumer.accept(_options);
		}
		
		try {
			_rDB = hardReadonly ?
					RocksDB.openReadOnly(_options, _folder.getAbsolutePath()) :
					RocksDB.open(_options, _folder.getAbsolutePath());
		} catch (RocksDBException e) {
			log.warn("RocksDB can't create", e);
			throw new RuntimeException(e);
		}

		name = _folder.getName();
		_softReadonly = true;
		uniqueObjects = null;
		_hardReadonly = hardReadonly;
	}
	
	protected RockingCache(RocksDB rDB, String name, boolean hardReadonly) {
		this.name = name;
		_folder = null;
		_rDB = rDB;
		_options = null;
		_softReadonly = true;
		_hardReadonly = hardReadonly;
		uniqueObjects = null;
	}

	public ColumnFamilyHandle createColumnFamily(String columnFamilyName) {
		synchronized (columnFamilyHandleMap) {
			try {
				final ColumnFamilyHandle columnFamilyHandle = _rDB.createColumnFamily(new ColumnFamilyDescriptor(columnFamilyName.getBytes(), new ColumnFamilyOptions()));
				columnFamilyHandleMap.put(columnFamilyName, columnFamilyHandle);
				return columnFamilyHandle;
			} catch (RocksDBException e) {
				log.error(e);
				throw new RuntimeException("Can't create column family");
			}
		}
	}

	public void dropColumnFamily(String columnFamilyName) {
		synchronized (columnFamilyHandleMap) {
			try {
				_rDB.dropColumnFamily(columnFamilyHandleMap.get(columnFamilyName));
			} catch (RocksDBException e) {
				log.error(e);
				throw new RuntimeException("Can't drop column family");
			}
			columnFamilyHandleMap.remove(columnFamilyName);
		}
	}

	public long getApproximateNumOfEntries() {
		long l = 0;

		try {
			l = _rDB.getLongProperty("rocksdb.estimate-num-keys");
		} catch (RocksDBException e) {
			e.printStackTrace();
		}

		return l;
	}

    /**
     * TODO: This is soft readonly function, we should implement a real readonly function to disable database directly.
     */
    public void setReadonly(boolean readonly) { _softReadonly = readonly; }
    public boolean isReadonly() { return _softReadonly | _hardReadonly; }

	/**
	 * ObjectUnique will try to keep only one RockingObject for each key in runtime.
     * When you enable ObjectUnique, you should only use object-relative functions,
     * because byte array is hard to track in runtime.
     *
     * You should enable ObjectUnique feature as early as possible, don't try to change
     * it frequently in runtime.
     *
     * Following functions are object-relative:
     * {@link #getObject(RockingKey, Class)}
     * {@link #getObject(long, Class)}
     * {@link #put(RockingObject)}
     * {@link #putAsync(RockingObject)}
     * {@link #putAsync(Collection)}
     * {@link #iteratorObjects(Class)}
     * {@link #iteratorObjects(RockingKey, Class)}
     * {@link #iteratorObjects(long, Class)}
     *
	 * @param enabled
	 */
	public void setObjectUnique(boolean enabled) {
        uniqueObjects = enabled ? new WeakHashMap<>() : null;
    }
    public boolean isObjectUnique() {
        return uniqueObjects != null;
    }

    public int sizeOfCachedObjects() {
	    return uniqueObjects == null ? 0 : uniqueObjects.size();
    }

    public String cacheStatus() {
	    return String.format("Hit:%d, Miss:%d, Error:%d", cacheHit, cacheMiss, cacheError);
    }

    private void _put(byte key[], byte value[]) {
        if (isReadonly()) {
            throw new UnsupportedOperationException(this.name + ": Readonly mode");
        }
        try {
            _rDB.put(key, value);
        } catch (RocksDBException e) {
            log.warn("RocksDB can't put", e);
            throw new RuntimeException(e);
        }
    }

	private void _put(byte key[], byte value[], String columnFamilyName) {
		if (isReadonly()) {
			throw new UnsupportedOperationException(this.name + ": Readonly mode");
		}
		try {
			if (columnFamilyHandleMap.size()==0) {
				_rDB.put(key, value);
			} else if (columnFamilyName == null) {
				_rDB.put(_rDB.getDefaultColumnFamily(), new WriteOptions(), key, value);
			} else {
				_rDB.put(columnFamilyHandleMap.get(columnFamilyName), new WriteOptions(), key, value);
			}
		} catch (RocksDBException e) {
			log.warn("RocksDB can't put", e);
			throw new RuntimeException(e);
		}
	}

    private void _pushToCache(RockingObject ro) {
        if (uniqueObjects != null) {
            synchronized(uniqueObjects) {
                WeakReference<RockingObject> ref = uniqueObjects.get(ro.key);
                if (ref != null) {
                    RockingObject refRO = ref.get();
                    if (refRO != null) {
                        if (refRO != ro) {
                            log.warn("Multiple rocking objects {0} and {1}", new Object[]{refRO, ro});
                            cacheError++;
                        } else {
                            return;
                        }
                    }
                }
                uniqueObjects.put(ro.key, new WeakReference<>(ro));
            }
        }
    }

    public void put(RockingObject ro) {
        _pushToCache(ro);
        _put(ro.keyBytes(), ro.valueBytes());
	}
	
	public void put(byte key[], byte value[]) {
        if (uniqueObjects != null) throw new UnsupportedOperationException("ObjectUnique mode");
        _put(key, value);
	}

	public void put(byte key[], byte value[], String columnFamilyName) {
		if (uniqueObjects != null) throw new UnsupportedOperationException("ObjectUnique mode");
		_put(key, value, columnFamilyName);
	}
	
	/**
	 * If use one of putAsync(...) functions, you should not
	 * change to use put(...) functions. If you want to use
	 * put(...) functions, use it after calling waitAsync().
	 * @param ro
	 */
	public void putAsync(RockingObject ro) {
        _pushToCache(ro);
		AsyncRO aro = new AsyncRO(ro);
		this.putAsync(aro);
	}

	/**
	 * If use one of putAsync(...) functions, you should not
	 * change to use put(...) functions. If you want to use
	 * put(...) functions, use it after calling waitAsync().
	 * @param ros
	 */
	public void putAsync(Collection<? extends RockingObject> ros) {
		if (ros.size()==0) return;
		if (uniqueObjects != null) ros.forEach(this::_pushToCache);
		AsyncROs aros = new AsyncROs(ros);
		this.putAsync(aros);
	}

	/**
	 * If use one of putAsync(...) functions, you should not
	 * change to use put(...) functions. If you want to use
	 * put(...) functions, use it after calling waitAsync().
	 * @param key
	 * @param value
	 */
	public void putAsync(byte key[], byte value[]) {
		AsyncKbVb akv = new AsyncKbVb(key, value);
		this.putAsync(akv);
	}
	
	public byte[] get(byte key[]) {
		try {
			return _rDB.get(key);
		} catch (RocksDBException e) {
			log.error("RocksDB can't load data" + e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public byte[] get(long longId) {
		byte kb[] = RockingObject.bytesFromLong(longId);
		return get(kb);
	}
	
	public Iterator<Map.Entry<byte[],byte[]>> iterator() {
		return iterator(null);
	}
	
	public Iterator<Map.Entry<byte[],byte[]>> iterator(long first) {
		byte kb[] = RockingObject.bytesFromLong(first);
		return iterator(kb);
	}
	
	public Iterator<Map.Entry<byte[],byte[]>> iterator(byte first[]) {
		final org.rocksdb.RocksIterator roIter = _rDB.newIterator();
		
		if (first==null) roIter.seekToFirst();
		else roIter.seek(first);
		
		return new Iterator<Map.Entry<byte[],byte[]>>() {

			@Override
			public boolean hasNext() {
				return roIter.isValid();
			}

			@Override
			public Map.Entry<byte[],byte[]> next() {
				byte ks[] = roIter.key();
				byte vs[] = roIter.value();

				Map.Entry<byte[],byte[]> t = new AbstractMap.SimpleEntry<>(ks, vs);

				roIter.next();
				
				return t;
			}

			@Override
			public void remove() {
				throw new java.lang.UnsupportedOperationException("Not support");
			}
			
		};
	}

	public Iterator<byte[]> iteratorKeys() {
		return iteratorKeys(null);
	}

	public Iterator<byte[]> iteratorKeys(long first) {
		byte kb[] = RockingObject.bytesFromLong(first);
		return iteratorKeys(kb);
	}

	public Iterator<byte[]> iteratorKeys(byte first[]) {
		final org.rocksdb.RocksIterator roIter = _rDB.newIterator();

		if (first==null) roIter.seekToFirst();
		else roIter.seek(first);

		return new Iterator<byte[]>() {

			@Override
			public boolean hasNext() {
				return roIter.isValid();
			}

			@Override
			public byte[] next() {
				byte ks[] = roIter.key();

				roIter.next();

				return ks;
			}

			@Override
			public void remove() {
				throw new java.lang.UnsupportedOperationException("Not support");
			}

		};
	}

    public <T extends RockingObject> T getObject(long longId, Class<T> cla) {
        return getObject(new RockingKey.LongID(longId), cla);
    }

    public <T extends RockingObject> T getObject(RockingKey key, Class<T> cla) {
        byte vb[]=null;
        try {
            if (uniqueObjects != null) {
                synchronized (uniqueObjects) {
                    WeakReference<RockingObject> ref = uniqueObjects.get(key);
                    if (ref != null) {
                        RockingObject refRO = ref.get();
                        if (refRO != null) {
                            cacheHit ++;
                            return cla.cast(refRO);
                        }
                    }
                    cacheMiss ++;
                    byte kb[] = key.toBytes();
                    vb = _rDB.get(kb);
                    T ro = vb==null ? null : cla.getConstructor(byte[].class, byte[].class).newInstance(kb, vb);
                    if (ro != null) {
                        uniqueObjects.put(ro.key, new WeakReference<>(ro));
                    }
                    return ro;
                }
            } else {
                byte kb[] = key.toBytes();
                vb = _rDB.get(kb);
                return vb==null ? null : cla.getConstructor(byte[].class, byte[].class).newInstance(kb, vb);
            }
        } catch (RocksDBException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            log.error("RocksDB can't load data ("+cla+")" + e);
			e.printStackTrace();

            MemoryPrinter.printMemory(log, Level.WARN, "key", key.toBytes(), 0, -1, 0);
            MemoryPrinter.printMemory(log, Level.WARN, "value", vb, 0, -1, 0);

            throw new RuntimeException(e);
        }
    }

    /**
     * Iterate objects
     * @return
     */
    public <T extends RockingObject> Iterator<T> iteratorObjects(Class<T> cla) {
        return iteratorObjects(null, cla);
    }

    public <T extends RockingObject> Iterator<T> iteratorObjects(long first, Class<T> cla) {
        return iteratorObjects(new RockingKey.LongID(first), cla);
    }

    public <T extends RockingObject> Iterator<T> iteratorObjects(RockingKey first, Class<T> cla) {
        final org.rocksdb.RocksIterator roIter = _rDB.newIterator();

        if (first==null) roIter.seekToFirst();
        else roIter.seek(first.toBytes());

        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return roIter.isValid();
            }

            @Override
            public T next() {
                byte ks[] = roIter.key();
                byte vs[] = null;
                T t = null;
                try {
                    if (uniqueObjects != null) {
                        synchronized (uniqueObjects) {
                            WeakReference<RockingObject> ref = uniqueObjects.get(ks);
                            if (ref != null) {
                                RockingObject refRO = ref.get();
                                if (refRO != null) {
                                    t = (T)refRO;
                                    cacheHit ++;
                                }
                            }
                            if (t == null) {
                                cacheMiss ++;

                                vs = roIter.value();
                                t = cla.getConstructor(byte[].class, byte[].class).newInstance(ks, vs);
                                uniqueObjects.put(t.key, new WeakReference<>(t));
                            }
                        }
                    } else {
                        vs = roIter.value();
                        t = cla.getConstructor(byte[].class, byte[].class).newInstance(ks, vs);
                    }
                } catch (InstantiationException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    log.error("RocksDB can't load data ("+cla+")" + e);
					e.printStackTrace();

                    if (ks != null) MemoryPrinter.printMemory(log, Level.WARN, "key", ks, 0, -1, 0);
                    if (vs != null) MemoryPrinter.printMemory(log, Level.WARN, "value", vs, 0, -1, 0);

                    throw new RuntimeException(e);
                }

                roIter.next();

                return t;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not support");
            }

        };
    }


    /**
     * Iterate keys
     * @return
     */
    public <T extends RockingKey> Iterator<T> iteratorObjectiveKeys(Class<T> cla) {
        return iteratorObjectiveKeys(null, cla);
    }

    // this function is redundant
    public <T extends RockingKey> Iterator<T> iteratorObjectiveKeys(long first, Class<T> cla) {
        return iteratorObjectiveKeys(new RockingKey.LongID(first), cla);
    }

    public <T extends RockingKey> Iterator<T> iteratorObjectiveKeys(RockingKey first, Class<T> cla) {
        final org.rocksdb.RocksIterator roIter = _rDB.newIterator();

        if (first==null) roIter.seekToFirst();
        else roIter.seek(first.toBytes());

        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return roIter.isValid();
            }

            @Override
            public T next() {
                byte ks[] = roIter.key();
                T t;

                try {
                    t = cla.getConstructor(byte[].class).newInstance(ks);
                } catch (InstantiationException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    log.warn("RocksDB can't load key ("+cla+")" + e);
					e.printStackTrace();

                    if (ks != null) MemoryPrinter.printMemory(log, Level.WARN, "key", ks, 0, -1, 0);

                    throw new RuntimeException(e);
                }

                roIter.next();

                return t;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not support");
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
				if (columnFamilyHandleMap!=null) {
					try {
						_rDB.dropColumnFamilies(new ArrayList<>(columnFamilyHandleMap.values()));
					} catch (RocksDBException e) {
						log.error(e);
					}
					columnFamilyHandleMap.clear();
				}
				try {
					_rDB.flush(new FlushOptions().setWaitForFlush(true).setAllowWriteStall(false));
					_rDB.closeE();
				} catch (Exception ex) {
					log.error(ex);
				}
				//_rDB = null;
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
				asyncList = new ArrayList<>();
				asyncT = a;
				ExecutorServices.es(ExecutorServices.DB_NAME).execute(a);
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
	
	private Async asyncT = null;
	private List<Async> asyncList = null;
	private Percentage asyncPercentage = null;
	protected abstract class Async implements Runnable {
		
		@Override
		public void run() {
			Async T = this;
			long max_dT = 0;
			
			try {
				while(true) {
					int size = T.size();
					long dT = System.currentTimeMillis();
					T.process();
					dT = (System.currentTimeMillis() - dT)/size;
					if (dT > max_dT) {
					    max_dT = dT;
                    }

					String perText;
					double value, total;
					synchronized(RockingCache.this) {
						asyncPercentage.addValue(size);
						perText = asyncPercentage.changedString();
						value = asyncPercentage.value();
						total = asyncPercentage.total();
						if (asyncList.size()==0) break;
						T = asyncList.remove(0);
					}
					
					if (perText!=null) {
						log.tracef("rDB(%s) saving %s=%d/%d, dT=%d", name, perText, (long)value, (long)total, dT);
						max_dT = 0;
					}
				}
			} catch(Exception e) {
				log.warn("rDB exception", e);
			} finally {
				synchronized(RockingCache.this) {
					if (asyncList.size()==0) {
						asyncT = null;
						asyncList = null;
						RockingCache.this.notifyAll();
						log.tracef("rDB(%s) saved 100%%", name);
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
			RockingCache.this._put(ro.keyBytes(), ro.valueBytes());
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
			for (RockingObject ro: ros) {
				RockingCache.this._put(ro.keyBytes(), ro.valueBytes());
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
			RockingCache.this._put(key, value);
		}

		@Override
		protected int size() {
			return 1;
		}
	}

}





