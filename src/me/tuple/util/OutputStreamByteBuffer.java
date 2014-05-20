package me.tuple.util;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamByteBuffer extends DynamicByteBuffer {

	final protected OutputStream outputStream;
	
	public OutputStreamByteBuffer(OutputStream output) {
		this(output, 64);
	}
	
	public OutputStreamByteBuffer(OutputStream output, int capacity) {
		super(capacity);
		outputStream = output; 
	}

	@Override
	public int position() { throw new UnsupportedOperationException();}
	
	@Override
	public DynamicByteBuffer position(int newPosition) { throw new UnsupportedOperationException();}
	
	
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
	protected void prepareForRead(int size) {throw new UnsupportedOperationException();}
	
	@Override
	protected void prepareForRead(int index, int size) {throw new UnsupportedOperationException();}
	
	@Override
	protected void prepareAllForRead() {throw new UnsupportedOperationException();}
	
	@Override
	protected void prepareForWrite(int size) {
		//int l = (_position+size) - _capacity;
		if ((_position+size) > _capacity) {
			// if buffer is full, write to stream first.
			try {
				outputStream.write(_data, 0, _position);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			compact();		// _position should be 0
		}
		limit(_position+size);		
	}
	
	@Override
	protected void prepareForWrite(int index, int size) {throw new UnsupportedOperationException();}
	
	@Override
	public void flushAllForWrite() {
		try {
			outputStream.write(_data, 0, _position);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		compact();		// _position should be 0
	}
	
	@Override
	protected void checkFullDataAlgorithmSupport() {throw new UnsupportedOperationException();}
	
	/**
	 * Call this to close internal stream, it will also call flushAllForWrite() first.
	 * Don't do anything anymore.
	 * @throws IOException
	 */
	public void close() throws IOException {
		flushAllForWrite();
		outputStream.close();
	}
	
}













