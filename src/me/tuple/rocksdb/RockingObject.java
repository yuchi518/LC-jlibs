package me.tuple.rocksdb;

import me.tuple.util.DynamicByteBuffer;

public abstract class RockingObject {

	public RockingObject() {
		
	}
	
	public RockingObject(byte[] keyBytes, byte[] valueBytes) {
		
	}
	
	public abstract byte[] keyBytes();
	public abstract byte[] valueBytes();
	
	
	/// HELPER FUNCTIONS
	
	public static byte[] vBytesFromLong(long l) {
		DynamicByteBuffer buf = new DynamicByteBuffer(9);
		buf.putVarLong(l);
		return buf.toBytesBeforeCurrentPosition();
	}
	
	public static long longFromvBytes(byte[] bs) {
		DynamicByteBuffer buf = new DynamicByteBuffer(bs, true);
		return buf.getVarLong();
	}
	
}
