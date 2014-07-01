package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class MultiPointMRC extends RecordContent {

	public MultiPointMRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 28;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
