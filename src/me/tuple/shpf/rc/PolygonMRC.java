package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PolygonMRC extends RecordContent {

	public PolygonMRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 25;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
