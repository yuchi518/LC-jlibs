package lets.cool.util;

public class Packages {
	final static byte RPCValueType_PACKAGE				= 0;
	final static byte RPCValueType_UINTEGER				= 1;	// var unsigned integer
	final static byte RPCValueType_SINTEGER				= 2;	// var signed integer
	final static byte RPCValueType_64BITS				= 3;	// fix 64 bits, only for double
	final static byte RPCValueType_32BITS				= 4;	// fix 32 bits, only for float
	final static byte RPCValueType_BYTES				= 5;	// with one UINT for length then data
	final static byte RPCValueType_COLLECTION			= 6;	// a collection with a number of values, it may contain anything. End with package end
	final static byte RPCValueType_COMPOSITE			= 7;	// a package, end with package end
	
	final static byte RPCPackageID_END				= 0;		// package end
	final static byte RPCPackageID_START			= 1;		// with one byte version, end with package end
	final static byte RPCPackageID_EXTEND			= 2;		// subclass, a new package, end with package end
	
	
	DynamicByteBuffer _buffer;
	
	public Packages() {
		_buffer = new DynamicByteBuffer(32);
	}
	
	
}




















