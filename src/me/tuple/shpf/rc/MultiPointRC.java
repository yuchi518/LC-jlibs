package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class MultiPointRC extends RecordContent {

	public MultiPointRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 8;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
