package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PolyLineRC extends RecordContent {

	public PolyLineRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 3;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
