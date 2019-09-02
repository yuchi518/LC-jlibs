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

import lets.cool.util.logging.Logr;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class RockingObjectsCache<TK extends RockingKey, TO extends RockingObject> extends RockingCache {

	final protected static Logr log = Logr.logger();

	final protected Class<TO> _objectClass;
	final protected Class<TK> _keyClass;

	protected RockingObjectsCache(Class<TK> kcla, Class<TO> cla, File folder) {
		this(kcla, cla, folder,null);
	}

	protected RockingObjectsCache(Class<TK> kcla, Class<TO> cla, File folder, Consumer<Options> optionsConsumer) {
		super(folder, optionsConsumer);
		_keyClass = kcla;
		_objectClass = cla;
		setObjectUnique(true);
	}

	protected RockingObjectsCache(Class<TK> kcla, Class<TO> cla, RocksDB rDB, String name) {
	    super(rDB, name);
		_keyClass = kcla;
		_objectClass = cla;
		setObjectUnique(true);
	}

    /**
     *
     * @param longId
     * @return
     * @see RockingCache#getObject(long, Class)
     */
	public TO getObject(long longId) {
		return getObject(longId, _objectClass);
	}

    /**
     *
     * @param key
     * @return
     * @see RockingCache#getObject(RockingKey, Class)
     */
	public TO getObject(RockingKey key) {
		return getObject(key, _objectClass);
	}

	/**
	 * @return
     * @see RockingCache#iteratorObjects(Class)
     */
    public Iterator<TO> iteratorObjects() {
		return iteratorObjects(_objectClass);
	}

    /**
     *
     * @param first
     * @return
     * @see RockingCache#iteratorObjects(long, Class)
     */
	public Iterator<TO> iteratorObjects(long first) {
		return iteratorObjects(first, _objectClass);
	}

    /**
     *
     * @param first
     * @return
     * @see RockingCache#iteratorObjects(RockingKey, Class)
     */
	public Iterator<TO> iteratorObjects(RockingKey first) {
		return iteratorObjects(first, _objectClass);
	}

    /**
     * @return
     * @see RockingCache#iteratorObjectiveKeys(Class)
     */
    public Iterator<TK> iteratorObjectiveKeys() {
        return iteratorObjectiveKeys(_keyClass);
    }

    /**
     *
     * @param first
     * @return
     * @see RockingCache#iteratorObjectiveKeys(long, Class)
     */
    public Iterator<TK> iteratorObjectiveKeys(long first) {
        return iteratorObjectiveKeys(first, _keyClass);
    }

    /**
     *
     * @param first
     * @return
     * @see RockingCache#iteratorObjectiveKeys(RockingKey, Class)
     */
    public Iterator<TK> iteratorObjectiveKeys(RockingKey first) {
        return iteratorObjectiveKeys(first, _keyClass);
    }
}





