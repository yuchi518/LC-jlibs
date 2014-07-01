package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class NullRC extends RecordContent {

	public NullRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 0;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
