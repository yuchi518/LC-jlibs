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

import lets.cool.util.DynamicByteBuffer;
import lets.cool.util.MemoryPrinter;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RockingObjectsCache<T extends RockingObject> extends RockingCache {

	protected static Logger log = Logger.getLogger(RockingObjectsCache.class.getName());

	final protected Class<T> _targetClass;

	protected RockingObjectsCache(Class<T> cla, File folder) {
		this(cla, folder,null);
	}
	protected RockingObjectsCache(Class<T> cla, File folder, Options options) {
		super(folder, options);
		_targetClass = cla;
	}

	protected RockingObjectsCache(Class<T> cla, RocksDB rDB, String name) {
	    super(rDB, name);
        _targetClass = cla;
	}

	public T getObject(long longId) {
		DynamicByteBuffer buff = new DynamicByteBuffer(8);
		buff.putVarLong(longId);
		byte kb[] = buff.toBytesBeforeCurrentPosition();
		buff.releaseForReuse();
		return getObject(kb);
	}

	public T getObject(byte key[]) {
		byte value[]=null;
        try {
			value = _rDB.get(key);
			return value==null ? null : _targetClass.getConstructor(byte[].class, byte[].class).newInstance(key, value);
		} catch (RocksDBException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.log(Level.WARNING, "RocksDB can't load data ("+_targetClass+")", e);

			MemoryPrinter.printMemory(log, Level.WARNING, "key", key, 0, -1, 0);
			MemoryPrinter.printMemory(log, Level.WARNING, "value", value, 0, -1, 0);

			throw new RuntimeException(e);
		}
	}

    /**
     * Iterate objects
     * @return
     */
	public Iterator<T> iteratorObjects() {
		return iteratorObjects(null);
	}

	public Iterator<T> iteratorObjects(long first) {
		DynamicByteBuffer buff = new DynamicByteBuffer(8);
		buff.putVarLong(first);
		byte kb[] = buff.toBytesBeforeCurrentPosition();
		buff.releaseForReuse();
		return iteratorObjects(kb);
	}

	public Iterator<T> iteratorObjects(byte first[]) {
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
					t = _targetClass.getConstructor(byte[].class, byte[].class).newInstance(ks, vs);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					log.log(Level.WARNING, "RocksDB can't load data ("+_targetClass+")", e);

					MemoryPrinter.printMemory(log, Level.WARNING, "key", ks, 0, -1, 0);
					MemoryPrinter.printMemory(log, Level.WARNING, "value", vs, 0, -1, 0);

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
}





