package me.tuple.rocksdb;

import java.io.File;

import me.tuple.util.DynamicByteBuffer;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;

public class RockingSetCache extends RockingCache {
	private byte[] TRUE_BYTE = new byte[]{1};
	
	public RockingSetCache(File folder) {
		super(folder);
	}

	public RockingSetCache(File folder, Options options) {
		super(folder, options);
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




