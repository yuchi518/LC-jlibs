package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class MultiPointZRC extends RecordContent {

	public MultiPointZRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 18;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
