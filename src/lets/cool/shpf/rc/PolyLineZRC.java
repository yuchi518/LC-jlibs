package lets.cool.shpf.rc;

import com.seisw.util.geom.Poly;
import lets.cool.shpf.RecordContent;

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
        throw new UnsupportedOperationException("Not implement yet.");
    }

    @Override
    public byte[] optimizedData() { throw new UnsupportedOperationException("Not implement yet."); }

    @Override
    public Poly poly() { throw new UnsupportedOperationException("Not implement yet."); }

}