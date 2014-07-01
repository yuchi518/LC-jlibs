package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PolyLineMRC extends RecordContent {

	public PolyLineMRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 23;
	}

	@Override
	public void parse() {
		throw new java.lang.UnsupportedOperationException("Not implement yet.");
	}

}
