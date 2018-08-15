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

public class RockingSetCache extends RockingCache {
	private byte[] TRUE_BYTE = new byte[]{1};
	
	public RockingSetCache(File folder) {
		super(folder);
	}

	public RockingSetCache(File folder, Consumer<Options> optionsConsumer) {
		super(folder, optionsConsumer);
	}
	
	protected RockingSetCache(RocksDB rDB, String name) {
		super(rDB, name);
	}
	
	public void set(long key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(9);
		buf.putVarLong(key);
		super.put(buf.toBytesBeforeCurrentPosition(), TRUE_BYTE);
		buf.releaseForReuse();
	}
	
	public boolean contains(long key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(9);
		buf.putVarLong(key);
		byte b[] = super.get(buf.toBytesBeforeCurrentPosition());
		buf.releaseForReuse();
		return b!=null && b.length==1 && b[0]!=0;
	}
	
	public void set(String key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		super.put(buf.toBytesBeforeCurrentPosition(), TRUE_BYTE);
		buf.releaseForReuse();
	}
	
	public boolean contains(String key) {
		DynamicByteBuffer buf = new DynamicByteBuffer(16);
		buf.putVarLengthString(key);
		byte b[] = super.get(buf.toBytesBeforeCurrentPosition());
		buf.releaseForReuse();
		return b!=null && b.length==1 && b[0]!=0;
	}
	
}




