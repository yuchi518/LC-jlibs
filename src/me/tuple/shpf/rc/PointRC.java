package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;

public class PointRC extends RecordContent {

	double x, y;
	
	public PointRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 1;
	}

	@Override
	public void parse() {
		x = input.getLEDouble();
		y = input.getLEDouble();
	}

}
