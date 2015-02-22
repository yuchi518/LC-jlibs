package me.tuple.shpf.rc;

import com.seisw.util.geom.Poly;
import me.tuple.shpf.RecordContent;
import me.tuple.util.DynamicByteBuffer;

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

    @Override
    public byte[] optimizedData() {
        if (optimizedData == null) {
            DynamicByteBuffer buff = new DynamicByteBuffer(rawData.length / 2);

            buff.putSignedVarLong((long) (x * BASE));
            buff.putSignedVarLong((long) (y * BASE));

            optimizedData = buff.toBytesBeforeCurrentPosition();
        }
        return  optimizedData;
    }

    @Override
    public Poly poly() { throw new UnsupportedOperationException("Not implement yet."); }

}
