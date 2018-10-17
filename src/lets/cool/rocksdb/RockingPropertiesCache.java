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
import java.util.function.Consumer;

import lets.cool.util.DynamicByteBuffer;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;

public class RockingPropertiesCache extends RockingCache {

	public RockingPropertiesCache(File folder) {
		super(folder);
	}

	public RockingPropertiesCache(File folder, Consumer<Options> optionsConsumer) {
		super(folder, optionsConsumer);
	}

	protected RockingPropertiesCache(RocksDB rDB, String name) {
		super(rDB, name);
	}
	
	public boolean contains(byte key[]) {
		return super.get(key)!=null;
	}
	
	public boolean contains(String key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return contains(kb);
	}
	
	
	public void setString(byte key[], String value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(value);
		byte vb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		super.put(key, vb);
	}
	
	public String getString(byte key[]) {
		byte vb[] = super.get(key);
		if (vb==null) return null;
		
		DynamicByteBuffer buf = new DynamicByteBuffer(vb, true);
		return buf.getVarLengthString();
	}
	
	public void setString(String key, String value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		setString(kb, value);
	}
	
	public String getString(String key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return getString(kb);
	}
	
	
	public void setLong(byte key[], long value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putUnsignedVarLong(value);
		byte vb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		super.put(key, vb);
	}
	
	/**
	 * If key-value doesn't exist, return default
	 * @param key
	 * @return
	 */
	public long getLong(byte key[], long defaultValue) {
		byte vb[] = super.get(key);
		if (vb==null) return defaultValue;
		
		DynamicByteBuffer buf = new DynamicByteBuffer(vb, true);
		return buf.getUnsignedVarLong();
	}
	
	public long getLong(byte key[]) {
		return getLong(key, 0);
	}
	
	
	public void setLong(String key, long value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		setLong(kb, value);
	}
	
	public long getLong(String key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return getLong(kb);
	}
	
	public long getLong(String key, long defaultValue) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return getLong(kb, defaultValue);
	}
	
	/// boolean
	
	public void set(byte key[], boolean value) {
		setLong(key, value?1:0);
	}
	
	public void set(String key, boolean value) { setLong(key, value?1:0); }
	
	public boolean is(byte key[]) {
		return getLong(key,0)!=0;
	}
	
	public boolean is(byte key[], boolean defaultValue) {
		return getLong(key,defaultValue?1:0)!=0;
	}
	
	public boolean is(String key, boolean defaultValue) {
		return getLong(key, defaultValue?1:0)!=0;
	}
	
	
	
	/// double
	
	public void setDouble(byte key[], double value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putDouble(value);
		byte vb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		super.put(key, vb);
	}
	
	public double getDouble(byte key[], double defaultValue) {
		byte vb[] = super.get(key);
		if (vb==null) return defaultValue;
		
		DynamicByteBuffer buf = new DynamicByteBuffer(vb, true);
		return buf.getDouble();
	}
	
	public double getDouble(byte key[]) {
		return getDouble(key, 0);
	}
	
	public double getDouble(String key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return getDouble(kb);
	}
	
	public double getDouble(String key, double defaultValue) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return getDouble(kb, defaultValue);
	}
	
	public void setDouble(String key, double value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		setDouble(kb, value);
	}
	
	
	// float
	public void setFloat(byte key[], float value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putFloat(value);
		byte vb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		super.put(key, vb);
	}
	
	public float getFloat(byte key[], float defaultValue) {
		byte vb[] = super.get(key);
		if (vb==null) return defaultValue;
		
		DynamicByteBuffer buf = new DynamicByteBuffer(vb, true);
		return buf.getFloat();
	}
	
	public float getFloat(byte key[]) {
		return getFloat(key, 0);
	}
	
	public float getFloat(String key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return getFloat(kb);
	}
	
	public float getFloat(String key, float defaultValue) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		return getFloat(kb, defaultValue);
	}
	
	public void setFloat(String key, float value) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte kb[] = buf.toBytesBeforeCurrentPosition();
		buf.releaseForReuse();
		
		setFloat(kb, value);
	}
	
}




















