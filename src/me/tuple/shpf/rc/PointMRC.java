package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PointMRC extends RecordContent {

	public PointMRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 21;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}