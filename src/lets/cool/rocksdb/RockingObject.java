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

import java.util.Objects;

public abstract class RockingObject<T extends RockingKey> implements Comparable<RockingObject> {

    /**
     * Key should not be modified after RockingObject initialized and it should not be kept in outside,
     * it will affect the life-cycle in cache.
     */
	final public T key;

    /**
     * To avoid key modified/accessed from outside, we should clone the key inside, but it waste memory and impact
     * performance. We preferred not to clone it by default, user should clone it in outside.
     * @param key
     */
	public RockingObject(T key) {
        this.key = key;
	}

    //public abstract RockingObject(byte[] keyBytes, byte[] valueBytes);

    /**
     * The reference returned by key() should not be kept in outside, it will affect the life-cycle in cache.
     * @return
     */
    final public T key() {
        return key;
    }

	final public byte[] keyBytes() {
	    return key.toBytes();
    }

	public abstract byte[] valueBytes();


    public static byte[] bytesFromLong(long l) {
        DynamicByteBuffer buf = new DynamicByteBuffer(9);
        buf.putUnsignedVarLong(l);
        byte bytes[] = buf.toBytesBeforeCurrentPosition();
        buf.releaseForReuse();;
        return bytes;
    }

    public static long longFromBytes(byte[] bs) {
        DynamicByteBuffer buf = new DynamicByteBuffer(bs, true);
        long l = buf.getUnsignedVarLong();
        buf.releaseForReuse();
        return l;
    }


    /**
     * 類別須完全一致才可以使用key作比較。
     * 如果子類別與子類別的子類別可視為相同物件，則子類別需要複寫此函式。
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RockingObject<?> that = (RockingObject<?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    /**
     * 類別須完全一致才可以使用key作比較。
     * 如果子類別與子類別的子類別可視為相同物件，則子類別需要複寫此函式。
     * @param o
     * @return
     */
    @Override
    public int compareTo(RockingObject o) {
        Class mC = this.getClass();
        Class oC = o.getClass();
        return mC==oC ? key.compareTo(o.key) : mC.toString().compareTo(oC.toString());
    }


    public static class BytesObject extends RockingObject<RockingKey.BytesID> {

        final public byte[] data;

        public BytesObject(byte[] keyBytes, byte[] valueBytes) {
            this(keyBytes, valueBytes, true);
        }

        public BytesObject(byte[] keyBytes, byte[] valueBytes, boolean clone) {
            super(new RockingKey.BytesID(keyBytes, clone));
            this.data = clone ? valueBytes.clone() : valueBytes;
        }

        @Override
        public byte[] valueBytes() {
            return data;
        }
    }
}
