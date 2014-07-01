package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PointZRC extends RecordContent {

	public PointZRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 11;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
