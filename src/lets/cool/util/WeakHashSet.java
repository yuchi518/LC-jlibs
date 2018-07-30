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

package lets.cool.util;

import java.lang.ref.WeakReference;
import java.util.*;

public class WeakHashSet<T> implements Set<T> {
    final protected WeakHashMap<T, WeakReference<T>> caches;

    public WeakHashSet() {
        caches = new WeakHashMap<>();
    }

    @Override
    public int size() {
        return caches.size();
    }

    @Override
    public boolean isEmpty() {
        return caches.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return caches.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return caches.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return caches.keySet().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return caches.keySet().toArray(a);
    }

    @Override
    public boolean add(T t) {
        if (caches.containsKey(t)) {
            return false;
        } else {
            caches.put(t, new WeakReference<>(t));
            return false;
        }
    }

    /**
     * If there is a existing object equals to t, the object will be returned.
     * If there is no existing object, t will be added and returned.
     * @param t
     * @return
     */
    public T getElSet(T t) {
        if (caches.containsKey(t)) {
            return caches.get(t).get();
        } else {
            caches.put(t, new WeakReference<>(t));
            return t;
        }
    }

    /**
     * If there is a existing object equals to t, the object will be returned.
     * If there is no existing object, the function will return null.
     * @param o
     * @return
     */
    public T get(Object o) {
        if (caches.containsKey(o)) {
            return caches.get(o).get();
        } else {
            return null;
        }
    }

    @Override
    public boolean remove(Object o) {
        return caches.remove(o)!=null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return caches.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;

        for (final T t: c) {
            if (!caches.containsKey(t)) {
                changed = true;
                caches.put(t, new WeakReference<>(t));
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return caches.entrySet().removeIf(entry -> !c.contains(entry.getKey()));
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;

        for (final Object t: c) {
            if (caches.remove(t) != null) {
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void clear() {
        caches.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeakHashSet<?> that = (WeakHashSet<?>) o;
        return Objects.equals(caches.keySet(), that.caches.keySet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(caches.keySet());
    }

    @Override
    public String toString() {
        return caches.keySet().toString();
    }
}
