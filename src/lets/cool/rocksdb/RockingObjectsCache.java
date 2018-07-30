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

import org.rocksdb.Options;
import org.rocksdb.RocksDB;

import java.io.File;
import java.util.*;
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
		setObjectUnique(true);
	}

	protected RockingObjectsCache(Class<T> cla, RocksDB rDB, String name) {
	    super(rDB, name);
        _targetClass = cla;
		setObjectUnique(true);
	}

    /**
     *
     * @param longId
     * @return
     * @see RockingCache#getObject(long, Class)
     */
	public T getObject(long longId) {
		return getObject(longId, _targetClass);
	}

    /**
     *
     * @param key
     * @return
     * @see RockingCache#getObject(RockingKey, Class)
     */
	public T getObject(RockingKey key) {
		return getObject(key, _targetClass);
	}

	/**
	 * @return
     * @see RockingCache#iteratorObjects(Class)
     */
    public Iterator<T> iteratorObjects() {
		return iteratorObjects(_targetClass);
	}

    /**
     *
     * @param first
     * @return
     * @see RockingCache#iteratorObjects(long, Class)
     */
	public Iterator<T> iteratorObjects(long first) {
		return iteratorObjects(first, _targetClass);
	}

    /**
     *
     * @param first
     * @return
     * @see RockingCache#iteratorObjects(RockingKey, Class)
     */
	public Iterator<T> iteratorObjects(RockingKey first) {
		return iteratorObjects(first, _targetClass);
	}
}





