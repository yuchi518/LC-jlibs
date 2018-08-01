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
import java.util.logging.Level;
import java.util.logging.Logger;

import lets.cool.util.*;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class RockingCache {

	protected static Logger log = Logger.getLogger(RockingCache.class.getName());

    public final String name;		// for debug
    protected final File _folder;
	protected RocksDB _rDB;
	protected boolean readonly;
	protected WeakHashMap<RockingKey, WeakReference<RockingObject>> uniqueObjects;
	protected int cacheHit = 0, cacheError = 0, cacheMiss = 0;
	
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
			.setTargetFileSizeMultiplier(2)
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
		readonly = true;
		uniqueObjects = null;
	}
	
	protected RockingCache(RocksDB rDB, String name) {
		_folder = null;
		_rDB = rDB;
		this.name = name;
        readonly = true;
		uniqueObjects = null;
	}

    /**
     * TODO: This is soft readonly function, we should implement a real readonly function to disable database directly.
     */
    public void setReadonly(boolean readonly) { this.readonly = readonly; }
    public boolean isReadonly() { return readonly; }

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
	    return String.format("Hit:%d, Miss:%d, Error:%d\n", cacheHit, cacheMiss, cacheError);
    }

    private void _put(byte key[], byte value[]) {
        if (readonly) {
            throw new UnsupportedOperationException(this.name + ": Readonly mode");
        }
        try {
            _rDB.put(key, value);
        } catch (RocksDBException e) {
            log.log(Level.WARNING, "RocksDB can't put", e);
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
                            log.log(Level.WARNING, "Multiple rocking objects {0} and {1}", new Object[]{refRO, ro});
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
			log.log(Level.WARNING, "RocksDB can't load data", e);
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

    public <T extends RockingObject> T getObject(long longId, Class<T> cla) {
        byte kb[] = RockingObject.bytesFromLong(longId);
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
                            return (T)refRO;
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
            log.log(Level.WARNING, "RocksDB can't load data ("+cla+")", e);

            MemoryPrinter.printMemory(log, Level.WARNING, "key", key.toBytes(), 0, -1, 0);
            MemoryPrinter.printMemory(log, Level.WARNING, "value", vb, 0, -1, 0);

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
                    log.log(Level.WARNING, "RocksDB can't load data ("+cla+")", e);

                    if (ks != null) MemoryPrinter.printMemory(log, Level.WARNING, "key", ks, 0, -1, 0);
                    if (vs != null) MemoryPrinter.printMemory(log, Level.WARNING, "value", vs, 0, -1, 0);

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
				_rDB.close();
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
	
	private void putAsync(Async a) {
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
			//int count=0;
			for (RockingObject ro: ros) {
				RockingCache.this._put(ro.keyBytes(), ro.valueBytes());
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
			RockingCache.this._put(key, value);
		}
		@Override
		protected int size() {
			return 1;
		}
	}

}





