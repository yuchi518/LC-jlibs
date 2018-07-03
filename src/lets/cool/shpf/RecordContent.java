package lets.cool.shpf;

import com.seisw.util.geom.Poly;
import lets.cool.util.DynamicByteBuffer;

public abstract class RecordContent {
    final public static long BASE = 1000000000L; // Use to covert double to long

    final public int recordNumber;
	final public byte rawData[];
	final protected DynamicByteBuffer input;
	public byte optimizedData[]=null;
	
	protected RecordContent(int recordNumber, byte dataWithNoCopy[]) {
		this.recordNumber = recordNumber;
		this.rawData = dataWithNoCopy;
		input = new DynamicByteBuffer(dataWithNoCopy, true);
		
		int type = input.getLEInt(); 
		if (type != shapeType()) {
			throw new RuntimeException("Type is not match, " + type + " != " + shapeType());
		}
	}
	
	public abstract int shapeType();
	
	// not include first four byte (shape type)
	public abstract void parse();
	
	public abstract byte[] optimizedData();

    public abstract Poly poly();

	@Override
	public int hashCode() {
		return recordNumber;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecordContent other = (RecordContent) obj;
		if (recordNumber != other.recordNumber)
			return false;
		return true;
	}
	
	
}





















