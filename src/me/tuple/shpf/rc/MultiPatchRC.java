package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class MultiPatchRC extends RecordContent {

	public MultiPatchRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 31;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
