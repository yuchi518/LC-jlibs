package me.tuple.util;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamByteBuffer extends DynamicByteBuffer {
	
	final protected InputStream inputStream;
	
	/**
	 * This is not the same as DynamicByteBuffer(InputStream) version.
	 * DynamicByteBuffer will load all data from inputstream, and 
	 * InputStreamByteBuffer will load data on the fly.
	 * @param input
	 */
	public InputStreamByteBuffer(InputStream input) {
		this(input, 64);
	}
	
	public InputStreamByteBuffer(InputStream input, int capacity) {
		super(capacity);
		inputStream = input;
	}
	
	@Override
	public int position() { throw new UnsupportedOperationException();}
	
	@Override
	public DynamicByteBuffer position(int newPosition) { throw new UnsupportedOperationException();}
	
	/**
	 * This is an estimate value, fully depend to the inputStream.available() behavior.
	 */
	@Override
	public int remaining() {
		try {
			return _limit - _position + inputStream.available();
		} catch (IOException e) {
			return _limit - _position;
		} 
	}
	
	@Override
	public boolean hasRemaining() {
		prepareForRead(1);
		return _limit - _position > 0;
	}
	
	@Override
	public DynamicByteBuffer mark() { throw new UnsupportedOperationException();}
	
	@Override
	public DynamicByteBuffer reset() {throw new UnsupportedOperationException();}
	
	@Override
	public DynamicByteBuffer clear() {throw new UnsupportedOperationException();}
	
	@Override
	public DynamicByteBuffer flip() {throw new UnsupportedOperationException();}
	
	@Override
	public DynamicByteBuffer rewind() {throw new UnsupportedOperationException();}
	
	
	@Override
	protected void prepareForRead(int size) {
		int l = (_position+size)-_limit;
		if (l>0) {
			/// read more after limit
			/// try to fill buffer 
			compact();		// _position should be 0
			
			if (size>_capacity) capacity(size);
			else l = _capacity-_limit;
			
			while(l>0) {
				try {
					int rL = inputStream.read(_data, _limit, l);
					if (rL<0) {
						// no more data
						break;
					}
					_limit += rL;
					l -= rL;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			
		}
	}
	
	@Override
	protected void prepareForRead(int index, int size) {throw new UnsupportedOperationException();}
	
	@Override
	protected void prepareAllForRead() {
		/// try to fill buffer 
		compact();		// _position should be 0
		int l = _capacity-_limit;
		while(true) {
			if (l==0) {
				capacity(_capacity*2);
				l = _capacity-_limit;
			}
			
			try {
				int rL = inputStream.read(_data, _limit, l);
				if (rL<0) {
					// no more data
					break;
				}
				_limit += rL;
				l -= rL;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
		}
		
	}
	
	@Override
	protected void prepareForWrite(int size) {throw new UnsupportedOperationException();}
	
	@Override
	protected void prepareForWrite(int index, int size) {throw new UnsupportedOperationException();}
	
	@Override
	protected void checkFullDataAlgorithmSupport() {throw new UnsupportedOperationException();}
	
	
	/**
	 * Call this to close internal stream, don't do anything anymore.
	 * @throws IOException
	 */
	public void close() throws IOException {
		inputStream.close();
	}
}







