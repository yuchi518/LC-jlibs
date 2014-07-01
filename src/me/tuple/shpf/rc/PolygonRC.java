package me.tuple.shpf.rc;

import me.tuple.shpf.RecordContent;
import me.tuple.util.DynamicByteBuffer;

public class PolygonRC extends RecordContent {

	public double Xmin, Ymin, Xmax, Ymax;
	public int NumParts, NumPoints;
	public int Parts[];
	public double Points[];
	
	public PolygonRC(int recordNumber, byte[] dataWithNoCopy) {
		super(recordNumber, dataWithNoCopy);
	}

	@Override
	public int shapeType() {
		return 5;
	}

	@Override
	public void parse() {
		Xmin = input.getLEDouble();
		Ymin = input.getLEDouble();
		Xmax = input.getLEDouble();
		Ymax = input.getLEDouble();
		
		NumParts = input.getLEInt();
		NumPoints = input.getLEInt();
		
		Parts = new int[NumParts];
		for (int i=0;i<NumParts;i++) Parts[i] = input.getLEInt();
		
		Points = new double[NumPoints*2];
		for (int i=0;i<NumPoints*2;i++) Points[i] = input.getLEDouble();
		
		if (input.hasRemaining()) throw new RuntimeException("Why still have data?");
		
		if (Xmin>=Xmax || Ymin>=Ymax)
			System.out.printf("Box (%f,%f,%f,%f) \n", Xmin, Ymin, Xmax, Ymax);
		
		///optimizedData();
	}

	final static long BASE = 1000000000L;
	@Override
	public byte[] optimizedData() {
		if (optimizedData==null) {
			DynamicByteBuffer buff = new DynamicByteBuffer(rawData.length/2);
			
			buff.putVarLong(this.shapeType());
			buff.putSignedVarLong((long)(Xmin*BASE));
			buff.putSignedVarLong((long)(Ymin*BASE));
			buff.putSignedVarLong((long)(Xmax*BASE)-(long)(Xmin*BASE));
			buff.putSignedVarLong((long)(Ymax*BASE)-(long)(Ymin*BASE));
			
			buff.putVarLong(NumParts);
			buff.putVarLong(NumPoints);
			
			int dI = 0;
			for (int i=0;i<NumParts;i++) {
				buff.putSignedVarLong(Parts[i]-dI);
				dI = Parts[i];
			}
			
			long dX=(long)(Xmin*BASE), dY=(long)(Ymin*BASE);
			for (int i=0;i<NumPoints*2;) {
				buff.putSignedVarLong((long)(Points[i]*BASE)-dX);
				dX = (long)(Points[i]*BASE);
				i++;
				buff.putSignedVarLong((long)(Points[i]*BASE)-dY);
				dY = (long)(Points[i]*BASE);
				i++;
				
			}
			
			optimizedData = buff.toBytesBeforeCurrentPosition();
			
			//System.out.println(rawData.length + " vs. " + optimizedData.length);
		}
		return optimizedData;
	}
	
	
}








