package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PolygonZRC extends RecordContent {

	public PolygonZRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 15;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
