package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PolyLineZRC extends RecordContent {

	public PolyLineZRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 13;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
