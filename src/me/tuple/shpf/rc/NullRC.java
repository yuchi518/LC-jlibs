package me.tuple.shpf.rc;

import com.seisw.util.geom.Poly;
import me.tuple.shpf.RecordContent;

public class NullRC extends RecordContent {

    public NullRC(int recordNumber, byte[] dataWithNoCopy) {
        super(recordNumber, dataWithNoCopy);
    }

    @Override
    public int shapeType() {
        return 0;
    }

    @Override
    public void parse() {
        throw new UnsupportedOperationException("Not implement yet.");
    }

    @Override
    public byte[] optimizedData() {
        throw new UnsupportedOperationException("Not implement yet.");
    }

    @Override
    public Poly poly() { throw new UnsupportedOperationException("Not implement yet."); }

}
