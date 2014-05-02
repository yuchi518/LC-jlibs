package me.tuple.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynamicByteBuffer {
	static Logger log  = Logger.getLogger(DynamicByteBuffer.class.getName());
	
	/**
	 * 0 <= mark <= position <= limit <= capacity
	 * 1. clear() makes a buffer ready for a new sequence of channel-read or relative put operations: 
	 * 		It sets the limit to the capacity and the position to zero.
	 * 2. flip() makes a buffer ready for a new sequence of channel-write or relative get operations:
	 * 		It sets the limit to the current position and then sets the position to zero.
	 * 3. rewind() makes a buffer ready for re-reading the data that it already contains: 
	 * 		It leaves the limit unchanged and sets the position to zero.
	 */
	
	byte _data[];
	int _capacity;
	int _limit;
	int _position;
	int _mark;
	
	public DynamicByteBuffer(int capacity) {
		_capacity = capacity;
		_data = new byte[_capacity];
		_limit = 0;
		_position = 0;
		_mark = 0;
	}
	
	public DynamicByteBuffer(byte data[], boolean noCopy) {
		if (data==null) data=new byte[0];
		
		if (noCopy) {
			_data = data;
		} else {
			_data = data.clone();
		}
		
		_capacity = _limit = _data.length;
		_position = _mark = 0;
	}
	
	public DynamicByteBuffer(InputStream input) {
		this(DynamicByteBuffer.readAllBytes(input),true);
	}
	
	public final int capacity()
	{
		return _capacity;
	}
	
	public final DynamicByteBuffer capacity(int newCapacity)
	{
		_capacity = newCapacity;
		
		if (_limit >= _capacity) {
			_limit = _capacity;
		}
		
		if (_position >= _capacity)
		{
			_position = _capacity;
		}
		
		if (_mark >= _capacity)
		{
			_mark = _capacity;
		}
		
		_data = Arrays.copyOf(_data, _capacity);
		
		return this;
	}
	
	public final int position()
	{
		return _position;
	}
	
	public final DynamicByteBuffer position(int newPosition)
	{
		if (newPosition > _limit) {
			throw new IllegalArgumentException("newPosition(" + newPosition + ") is larger than limit(" + _limit + ")");
		}
		
		if (newPosition < _mark) {
			// discard or set the same value
			_position = _mark;
		} else {
			_position = newPosition;
		}
		
		return this;
	}
	
	public final int limit()
	{
		return _limit;
	}
	
	public final DynamicByteBuffer limit(int newLimit)
	{
		if (newLimit < 0) {
			throw new IllegalArgumentException("newLimit(" + newLimit + ") is les than 0");
		}
		
		if (newLimit > _capacity) {
			//_limit = _capacity;
			// expand size
			this.capacity(newLimit+32);
		}
		
		_limit = newLimit;
		
		if (_mark > _limit) {
			_mark = _limit;
		}
		
		if (_position > _limit) {
			_position = _limit;
		}
		
		return this;
	}
	
	public final DynamicByteBuffer mark()
	{
		_mark = _position;
		
		return this;
	}
	
	/**
	 * Go to mark position.
	 * @return
	 */
	public final DynamicByteBuffer reset()
	{
		_position = _mark;
		
		return this;
	}
	
	/**
	 * A new sequence, all reset to default.
	 * Clears this buffer. The position is set to zero, the limit is set to the capacity, and the mark is discarded.
	 * @return
	 */
	public final DynamicByteBuffer clear()
	{
		_position = 0;
		_mark = 0;
		_limit = _capacity;
		return this;
	}
	
	/**
	 * Flips this buffer. The limit is set to the current position and then the position is set to zero. 
	 * If the mark is defined then it is discarded.
	 * @return
	 */
	public final DynamicByteBuffer flip()
	{
		_limit = _position;
		_position = 0;
		_mark = 0;
		return this;
	}
	
	/**
	 * Repeat a sequence again. The position is set to zero and the mark is discarded.
	 * @return
	 */
	public final DynamicByteBuffer rewind()
	{
		_position = 0;
		_mark = 0;
		
		return this;
	}
	
	/**
	 * The bytes between the buffer's current position and its limit, if any, are copied to the beginning of the buffer.
	 * @return
	 */
	public DynamicByteBuffer compact() {
		System.arraycopy(_data, _position, _data, 0, _limit-_position);
		_position = _limit - _position;
		_limit = _capacity;
		_mark = 0;
		return this;
	}
	
	public final int remaining()
	{
		return _limit - _position;
	}
	
	public final boolean hasRemaining()
	{
		return (_limit - _position) > 0;
	}
	
	public boolean isReadOnly()
	{
		return false;
	}
	
	public boolean hasArray()
	{
		return true;
	}
	
	public byte[] array()
	{
		return _data;
	}
	
	public int arrayOffset()
	{
		return 0;
	}
	
	public boolean isDirect()
	{
		return false;
	}
	
	
	
	public CharBuffer asCharBuffer() {
		// TODO: Auto-generated method stub
		return null;
	}

	
	public DoubleBuffer asDoubleBuffer() {
		// TODO: Auto-generated method stub
		return null;
	}

	
	public FloatBuffer asFloatBuffer() {
		// TODO: Auto-generated method stub
		return null;
	}

	
	public IntBuffer asIntBuffer() {
		// TODO: Auto-generated method stub
		return null;
	}

	
	public LongBuffer asLongBuffer() {
		// TODO: Auto-generated method stub
		return null;
	}

	
	public DynamicByteBuffer asReadOnlyBuffer() {
		// TODO: Auto-generated method stub
		return null;
	}

	
	public ShortBuffer asShortBuffer() {
		// TODO: Auto-generated method stub
		return null;
	}

	
	
	public byte get() {
		if (_position >= _limit) {
			throw new BufferUnderflowException();
		}
		
		return _data[_position++];
	}
	
	public byte peek() {
		if (_position >= _limit) {
			throw new BufferUnderflowException();
		}
		
		return _data[_position];
	}
	
	public byte get(int index) {
		if (index<0 || index>=_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return _data[index];
	}
	
	public boolean getBoolean() {
		return get()!=0;
	}
	
	public boolean getBoolean(int index) {
		return get(index)!=0;
	}

	
	/*public char getChar() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public char getChar(int index) {
		// TODO Auto-generated method stub
		return 0;
	}*/

	
	public int getInt() {
		if (_position<0 || _position+4>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		int i;
		i = ((_data[_position++]&0x00ff) << 24);
		i |= ((_data[_position++]&0x00ff) << 16);
		i |= ((_data[_position++]&0x00ff) << 8);
		i |= ((_data[_position++]&0x00ff) << 0);
		
		return i;
	}
	
	public int get3bytesInt() {
		if (_position<0 || _position+3>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		int i;
		i = ((_data[_position++]&0x00ff) << 16);
		i |= ((_data[_position++]&0x00ff) << 8);
		i |= ((_data[_position++]&0x00ff) << 0);
		
		return i;
	}

	public int peekInt() {
		if (_position<0 || _position+4>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return (((_data[_position]&0x00ff) << 24) |
				((_data[_position+1]&0x00ff) << 16) |
				((_data[_position+2]&0x00ff) << 8) |
				((_data[_position+3]&0x00ff) << 0));
	}
	
	public int peek3bytesInt() {
		if (_position<0 || _position+3>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return (((_data[_position+0]&0x00ff) << 16) |
				((_data[_position+1]&0x00ff) << 8) |
				((_data[_position+2]&0x00ff) << 0));
	}
	
	public int getInt(int index) {
		if (index<0 || index+4>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return (((_data[index]&0x00ff) << 24) |
				((_data[index+1]&0x00ff) << 16) |
				((_data[index+2]&0x00ff) << 8) |
				((_data[index+3]&0x00ff) << 0));
	}

	public int get3bytesInt(int index) {
		if (index<0 || index+3>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return (((_data[index+0]&0x00ff) << 16) |
				((_data[index+1]&0x00ff) << 8) |
				((_data[index+2]&0x00ff) << 0));
	}
	
	public long get5bytesLong() {
		if (_position<0 || _position+5>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		long l;
		//l = (((long)_data[_position++]&0x00ff) << 56);
		//l |= (((long)_data[_position++]&0x00ff) << 48);
		//l |= (((long)_data[_position++]&0x00ff) << 40);
		l = (((long)_data[_position++]&0x00ff) << 32);
		l |= (((long)_data[_position++]&0x00ff) << 24);
		l |= (((long)_data[_position++]&0x00ff) << 16);
		l |= (((long)_data[_position++]&0x00ff) << 8);
		l |= (((long)_data[_position++]&0x00ff) << 0);
		
		return l;
	}

	public long get6bytesLong() {
		if (_position<0 || _position+6>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		long l;
		//l = (((long)_data[_position++]&0x00ff) << 56);
		//l |= (((long)_data[_position++]&0x00ff) << 48);
		l = (((long)_data[_position++]&0x00ff) << 40);
		l |= (((long)_data[_position++]&0x00ff) << 32);
		l |= (((long)_data[_position++]&0x00ff) << 24);
		l |= (((long)_data[_position++]&0x00ff) << 16);
		l |= (((long)_data[_position++]&0x00ff) << 8);
		l |= (((long)_data[_position++]&0x00ff) << 0);
		
		return l;
	}

	public long get7bytesLong() {
		if (_position<0 || _position+7>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		long l;
		//l = (((long)_data[_position++]&0x00ff) << 56);
		l = (((long)_data[_position++]&0x00ff) << 48);
		l |= (((long)_data[_position++]&0x00ff) << 40);
		l |= (((long)_data[_position++]&0x00ff) << 32);
		l |= (((long)_data[_position++]&0x00ff) << 24);
		l |= (((long)_data[_position++]&0x00ff) << 16);
		l |= (((long)_data[_position++]&0x00ff) << 8);
		l |= (((long)_data[_position++]&0x00ff) << 0);
		
		return l;
	}
	
	public long getLong() {
		if (_position<0 || _position+8>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		long l;
		l = (((long)_data[_position++]&0x00ff) << 56);
		l |= (((long)_data[_position++]&0x00ff) << 48);
		l |= (((long)_data[_position++]&0x00ff) << 40);
		l |= (((long)_data[_position++]&0x00ff) << 32);
		l |= (((long)_data[_position++]&0x00ff) << 24);
		l |= (((long)_data[_position++]&0x00ff) << 16);
		l |= (((long)_data[_position++]&0x00ff) << 8);
		l |= (((long)_data[_position++]&0x00ff) << 0);
		
		return l;
	}

	public long peekLong() {
		if (_position<0 || _position+8>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return ((((long)_data[_position]&0x00ff) << 56) |
				(((long)_data[_position+1]&0x00ff) << 48) |
				(((long)_data[_position+2]&0x00ff) << 40) |
				(((long)_data[_position+3]&0x00ff) << 32) |
				(((long)_data[_position+4]&0x00ff) << 24) |
				(((long)_data[_position+5]&0x00ff) << 16) |
				(((long)_data[_position+6]&0x00ff) << 8) |
				(((long)_data[_position+7]&0x00ff) << 0));
	}

	
	public long getLong(int index) {
		if (index<0 || index+8>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return ((((long)_data[index]&0x00ff) << 56) |
				(((long)_data[index+1]&0x00ff) << 48) |
				(((long)_data[index+2]&0x00ff) << 40) |
				(((long)_data[index+3]&0x00ff) << 32) |
				(((long)_data[index+4]&0x00ff) << 24) |
				(((long)_data[index+5]&0x00ff) << 16) |
				(((long)_data[index+6]&0x00ff) << 8) |
				(((long)_data[index+7]&0x00ff) << 0));
	}

	
	public short getShort() {
		if (_position<0 || _position+2>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		short s;
		s = (short)((_data[_position++]&0x00ff) << 8);
		s |= (short)((_data[_position++]&0x00ff) << 0);
		
		return s;
	}
	
	public short peekShort() {
		if (_position<0 || _position+2>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return (short)(((_data[_position]&0x00ff) << 8) |
				((_data[_position+1]&0x00ff) << 0));
	}
	
	public short getShort(int index) {
		if (index<0 || index+2>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		return (short)(((_data[index]&0x00ff) << 8) |
				((_data[index+1]&0x00ff) << 0));
	}
	
	// get bytes
	
	public byte[] getLastBytes() {
		byte b[];
		
		if (_limit-_position<=0) return null;
		
		b = Arrays.copyOfRange(_data, _position, _limit);
		_position = _limit;
		
		return b;
	}
	
	public byte[] getBytesWithFixedLength(int length) {
		if (_position<0 || _position+length>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		if (length==0) return null;
		
		byte b[];
		
		b = Arrays.copyOfRange(_data, _position, _position+length);
		_position += length;
		
		return b;
	}
	
	public byte[] getBytesWithOneByteLength() {
		if (_position<0 || _position+1>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		int length = _data[_position++];
		
		if (length==0) return null;
		
		if (_position<0 || _position+length>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		byte b[];
		
		b = Arrays.copyOfRange(_data, _position, _position+length);
		_position += length;
		
		return b;
	}
	
	public byte[] getBytesWithTwoBytesLength() {
		if (_position<0 || _position+2>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		int length = ((int)_data[_position++] & 0x00ff) << 8;
		length |= ((int)_data[_position++] & 0x00ff);
		
		if (length==0) return null;
		
		if (_position<0 || _position+length>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		byte b[];
		
		b = Arrays.copyOfRange(_data, _position, _position+length);
		_position += length;
		
		return b;
	}
	
	
	
	/*
	public byte[] getBytesWithRemainingLength(int remainingLength) {
		int to = _limit - remainingLength;
		
		if (to<_position || to>=_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		byte b[];
		
		b = Arrays.copyOfRange(_data, _position, to);
		_position = to;
		
		return b;
	}
	*/
	
	// get string
	public String getLastString() {
		if (_limit-_position<=0) return null;
		
		String s;
		try {
			s = new String(_data, _position, _limit-_position, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported decoded", e);
			s = new String(_data, _position, _limit-_position);
		}
		
		_position = _limit;
		
		return s;
	}
	
	public String getStringWithFixedLength(int length) {
		if (length<=0) return null;
		
		String s;
		try {
			s = new String(_data, _position, length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported decoded", e);
			s = new String(_data, _position, length);
		}
		
		_position += length;
		
		return s;
	}
	
	public String getStringWithOneByteLength() {
		if (_position<0 || _position+1>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		int length = _data[_position++];
		
		if (length==0) return null;
		
		if (_position<0 || _position+length>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		String s;
		try {
			s = new String(_data, _position, length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported decoded", e);
			s = new String(_data, _position, length);
		}
		
		_position += length;
		
		return s;
	}
	
	public String getStringWithTwoBytesLength() {
		if (_position<0 || _position+2>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		int length = ((int)_data[_position++] & 0x00ff) << 8;
		length |= ((int)_data[_position++] & 0x00ff);
		
		if (length==0) return null;
		
		if (_position<0 || _position+length>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		String s;
		try {
			s = new String(_data, _position, length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported decoded", e);
			s = new String(_data, _position, length);
		}
		
		_position += length;
		
		return s;
	}
	
	/*public String getStringWithRemainingLength(int remainingLength) {
		int length = _limit - remainingLength - _position;
		
		if (length<0 || _position+length>_limit) {
			throw new IndexOutOfBoundsException();
		}
		
		String s;
		try {
			s = new String(_data, _position, length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported decoded", e);
			s = new String(_data, _position, length);
		}
		
		_position += length;
		
		return s;
	}*/
	
	public DynamicByteBuffer put(byte b) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		
		if (_position+1>_limit) {
			limit(_position+1);
		}
		
		_data[_position++] = b;
		
		return this;
	}
	
	
	public DynamicByteBuffer put(int index, byte b) {
		if (index<0) {
			throw new IndexOutOfBoundsException();
		}
		if (index+1>_limit) {
			limit(index+1);
		}
		
		_data[index] = b;
		
		return this;
	}

	public DynamicByteBuffer put(boolean b) {
		return put(b?(byte)1:(byte)0);
	}
	
	public DynamicByteBuffer put(int index, boolean b) {
		return put(index, b?(byte)1:(byte)0);
	}
	
	/*public DynamicByteBuffer putChar(char value) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public DynamicByteBuffer putChar(int index, char value) {
		short s = (short)value;
	}*/

	
	public DynamicByteBuffer putInt(int value) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		
		if (_position+4>_limit) {
			limit(_position+4);
		}
		
		_data[_position++] = (byte)((value>>24) & 0xff);
		_data[_position++] = (byte)((value>>16) & 0xff);
		_data[_position++] = (byte)((value>>8) & 0xff);
		_data[_position++] = (byte)(value & 0xff);
		
		return this;
	}

	public DynamicByteBuffer put3bytesInt(int value) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		
		if (_position+3>_limit) {
			limit(_position+3);
		}
		
		_data[_position++] = (byte)((value>>16) & 0xff);
		_data[_position++] = (byte)((value>>8) & 0xff);
		_data[_position++] = (byte)(value & 0xff);
		
		return this;
	}

	public DynamicByteBuffer putInt(int index, int value) {
		if (index<0) {
			throw new IndexOutOfBoundsException();
		}
		if (index+4>_limit) {
			limit(index+4);
		}
		
		_data[index] = (byte)((value>>24) & 0xff);
		_data[index+1] = (byte)((value>>16) & 0xff);
		_data[index+2] = (byte)((value>>8) & 0xff);
		_data[index+3] = (byte)(value & 0xff);
		
		return this;
	}
	
	
	public DynamicByteBuffer put3bytesInt(int index, int value) {
		if (index<0) {
			throw new IndexOutOfBoundsException();
		}
		if (index+3>_limit) {
			limit(index+3);
		}
		
		_data[index+0] = (byte)((value>>16) & 0xff);
		_data[index+1] = (byte)((value>>8) & 0xff);
		_data[index+2] = (byte)(value & 0xff);
		
		return this;
	}

	
	public DynamicByteBuffer put5bytesLong(long value) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		if (_position+5>_limit) {
			limit(_position+5);
		}
		
		//_data[_position++] = (byte)((value>>56) & 0xff);
		//_data[_position++] = (byte)((value>>48) & 0xff);
		//_data[_position++] = (byte)((value>>40) & 0xff);
		_data[_position++] = (byte)((value>>32) & 0xff);
		_data[_position++] = (byte)((value>>24) & 0xff);
		_data[_position++] = (byte)((value>>16) & 0xff);
		_data[_position++] = (byte)((value>>8) & 0xff);
		_data[_position++] = (byte)(value & 0xff);
		
		return this;
	}

	public DynamicByteBuffer put6bytesLong(long value) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		if (_position+6>_limit) {
			limit(_position+6);
		}
		
		//_data[_position++] = (byte)((value>>56) & 0xff);
		//_data[_position++] = (byte)((value>>48) & 0xff);
		_data[_position++] = (byte)((value>>40) & 0xff);
		_data[_position++] = (byte)((value>>32) & 0xff);
		_data[_position++] = (byte)((value>>24) & 0xff);
		_data[_position++] = (byte)((value>>16) & 0xff);
		_data[_position++] = (byte)((value>>8) & 0xff);
		_data[_position++] = (byte)(value & 0xff);
		
		return this;
	}

	public DynamicByteBuffer put7bytesLong(long value) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		if (_position+7>_limit) {
			limit(_position+7);
		}
		
		//_data[_position++] = (byte)((value>>56) & 0xff);
		_data[_position++] = (byte)((value>>48) & 0xff);
		_data[_position++] = (byte)((value>>40) & 0xff);
		_data[_position++] = (byte)((value>>32) & 0xff);
		_data[_position++] = (byte)((value>>24) & 0xff);
		_data[_position++] = (byte)((value>>16) & 0xff);
		_data[_position++] = (byte)((value>>8) & 0xff);
		_data[_position++] = (byte)(value & 0xff);
		
		return this;
	}

	public DynamicByteBuffer putLong(long value) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		if (_position+8>_limit) {
			limit(_position+8);
		}
		
		_data[_position++] = (byte)((value>>56) & 0xff);
		_data[_position++] = (byte)((value>>48) & 0xff);
		_data[_position++] = (byte)((value>>40) & 0xff);
		_data[_position++] = (byte)((value>>32) & 0xff);
		_data[_position++] = (byte)((value>>24) & 0xff);
		_data[_position++] = (byte)((value>>16) & 0xff);
		_data[_position++] = (byte)((value>>8) & 0xff);
		_data[_position++] = (byte)(value & 0xff);
		
		return this;
	}

	
	public DynamicByteBuffer putLong(int index, long value) {
		if (index<0) {
			throw new IndexOutOfBoundsException();
		}
		if (index+8>_limit) {
			limit(index+8);
		}
		
		_data[index] = (byte)((value>>56) & 0xff);
		_data[index+1] = (byte)((value>>48) & 0xff);
		_data[index+2] = (byte)((value>>40) & 0xff);
		_data[index+3] = (byte)((value>>32) & 0xff);
		_data[index+4] = (byte)((value>>24) & 0xff);
		_data[index+5] = (byte)((value>>16) & 0xff);
		_data[index+6] = (byte)((value>>8) & 0xff);
		_data[index+7] = (byte)(value & 0xff);
		
		return this;
	}

	
	public DynamicByteBuffer putShort(short value) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		if (_position+2>_limit) {
			limit(_position+2);
		}
		
		_data[_position++] = (byte)((value>>8) & 0xff);
		_data[_position++] = (byte)(value & 0xff);
		
		return this;
	}

	
	public DynamicByteBuffer putShort(int index, short value) {
		if (index<0) {
			throw new IndexOutOfBoundsException();
		}
		if (index+2>_limit) {
			limit(index+2);
		}
		
		_data[index] = (byte)((value>>8) & 0xff);
		_data[index+1] = (byte)(value & 0xff);
		
		return this;
	}
	
/// === DOUBLE, FLOAT
	public DynamicByteBuffer putDouble(double value) {
		//return putLong(Double.doubleToLongBits(value));
		return putLong(Double.doubleToRawLongBits(value));
	}
	
	public DynamicByteBuffer putDouble(int index, double value) {
		//return putLong(index, Double.doubleToLongBits(value));
		return putLong(index, Double.doubleToRawLongBits(value));
	}

	public DynamicByteBuffer putFloat(float value) {
		//return putInt(Float.floatToIntBits(value));
		return putInt(Float.floatToRawIntBits(value));
	}
	
	public DynamicByteBuffer putFloat(int index, float value) {
		//return putInt(index, Float.floatToIntBits(value));
		return putInt(index, Float.floatToRawIntBits(value));
	}
	
	public double getDouble() {
		return Double.longBitsToDouble(getLong());
	}
	
	public double peekDouble() {
		return Double.longBitsToDouble(peekLong());
	}
	
	public double getDouble(int index) {
		return Double.longBitsToDouble(getLong(index));
	}

	public float getFloat() {
		return Float.intBitsToFloat(getInt());
	}
	
	public float peekFloat() {
		return Float.intBitsToFloat(peekInt());
	}
	
	public float getFloat(int index) {
		return Float.intBitsToFloat(getInt(index));
	}

	/*public DynamicByteBuffer putFloatValue(float value) {
		putInt(Float.floatToRawIntBits(value));
		return this;
	}
	
	public float getFloatValue() {
		return Float.intBitsToFloat(getInt());
	}
	
	public DynamicByteBuffer putDoubleValue(double value) {
		putLong(Double.doubleToRawLongBits(value));
		return this;
	}
	
	public double getDoubleValue() {
		return Double.longBitsToDouble(getLong());
	}*/
	
	///
	
	public DynamicByteBuffer putLastBytes(byte bytes[]) {
		if (bytes==null) return this;
		
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		
		int l = bytes==null?0:bytes.length;
		
		if (_position+l>_limit) {
			limit(_position+l);
		}
		
		if (l>0) {
			System.arraycopy(bytes, 0, _data, _position, bytes.length);
			_position += bytes.length;			
		}
		
		return this;
	}
	
	public DynamicByteBuffer putBytesWithOneByteLength(byte bytes[]) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		
		int l = bytes==null?0:bytes.length;
		
		if (l>255) {
			throw new IllegalArgumentException("Put data length(" + l + ") > 255");
		}
		
		
		if (_position+l+1>_limit) {
			limit(_position+l+1);
		}
		
		_data[_position++] = (byte)l;
		
		if (l>0) {
			System.arraycopy(bytes, 0, _data, _position, bytes.length);
			_position += bytes.length;
		}
		
		return this;
	}
	
	public DynamicByteBuffer putBytesWithTwoBytesLength(byte bytes[]) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		
		int l = bytes==null?0:bytes.length;
		
		if (l>65535) {
			throw new IllegalArgumentException("Put data length(" + l + ") > 65535");
		}
		
		
		if (_position+l+2>_limit) {
			limit(_position+l+2);
		}
		
		_data[_position++] = (byte)((l>>8) & 0xff);
		_data[_position++] = (byte)(l & 0xff);
		
		if (l>0) {
			System.arraycopy(bytes, 0, _data, _position, bytes.length);
			_position += bytes.length;
		}		
		
		return this;
	}
	
	public DynamicByteBuffer putLastString(String s) {
		try {
			return putLastBytes(s==null?null:s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported encoded", e);
			return putLastBytes(s.getBytes());
		}
	}
	
	public DynamicByteBuffer putStringWithOneByteLength(String s) {
		try {
			return putBytesWithOneByteLength(s==null?null:s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported encoded", e);
			return putBytesWithOneByteLength(s.getBytes());
		}
	}
	
	public DynamicByteBuffer putStringWithTwoBytesLength(String s) {
		try {
			return putBytesWithTwoBytesLength(s==null?null:s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported encoded", e);
			return putBytesWithTwoBytesLength(s.getBytes());
		}
	}
	
	public DynamicByteBuffer slice() {
		// TODO: Auto-generated method stub
		return null;
	}
	
	/**
	 * From: mark, To: position
	 * @return
	 */
	public byte[] toBytesBeforeCurrentPosition() {
		return Arrays.copyOfRange(_data, _mark, _position);
	}
	
	public ByteBuffer toByteBufferBeforeCurrentPosition() {
		return ByteBuffer.wrap(toBytesBeforeCurrentPosition());
	}
	
	public void toOutputStream(OutputStream out) throws IOException {
		out.write(_data, _mark, _position-_mark);
	}
	
	
	
	/// ================= Variable, Non-fixed length variable =====================
	
	public static class IndexAnd4bitsType
	{
		public IndexAnd4bitsType() {
			
		}
		
		public IndexAnd4bitsType(long v, byte t) {
			value = v;
			RPCValueType = t;
		}
		
		public long value;
		public byte RPCValueType;
	}
	
	public DynamicByteBuffer putIndexAnd4bitsType(IndexAnd4bitsType vat) {
		if (vat.value>=0 && vat.value <= 0x07) {													// 0 ~ 0x07
			put((byte)((vat.RPCValueType<<4) | vat.value));
		} else if (vat.value>=0 && vat.value <= 0x03FF+(0x08)) {									// 0x08 ~ 0x3FF+0x08
			putShort((short)((vat.RPCValueType<<12) | 0x0800 | (vat.value-0x08)));
		} else if (vat.value>=0 && vat.value <= 0x01FFFF+0x0408) {									// 0x0408 ~ 0x01FFFF+0x0408
			put3bytesInt((int)((vat.RPCValueType<<20) | 0x0C0000 | (vat.value-0x0408)));
		} else if (vat.value>=0 && vat.value <= 0x00FFFFFF+0x020408) {								// 0x020408 ~ 0x00FFFFFF+0x020408
			putInt((int)((vat.RPCValueType<<28) | 0x0E000000 | (vat.value-0x020408)));
		} else {
			//throw new RuntimeException("Type value(%lld) to long", value);
			//EXCEPTIONv(@"Type value(%lld) to long", value);
		}
		return this;
	}
	
	public IndexAnd4bitsType getIndexAnd4bitsType() {
		byte t = get();
		IndexAnd4bitsType vat = new IndexAnd4bitsType();
		vat.RPCValueType = (byte)((t >> 4) & 0x0F);
		if ((t&0x08)==0) {
			vat.value = t & 0x07;
		} else if ((t&0x04)==0) {
			vat.value = (((t&0x03)<<8) | (get()&0x00FF)) + 0x08;
		} else if ((t&0x02)==0) {
			vat.value = (((t&0x01)<<16) | (getShort()&0x00FFFF)) + 0x0408;
		} else if ((t&0x01)==0) {
			vat.value = (((t&0x00)<<24) | (get3bytesInt()&0x00FFFFFF)) + 0x020408;
		} else {
			//EXCEPTIONv(@"Type value(%d) to long", vat.type);
		}
		
		return vat;
		
	}
	
	public DynamicByteBuffer putVarLong(long value) {
		if (value>=0 && value<=0x7F) {								// 0 ~ 0x7F
			put((byte)value);
		} else if (value>=0 && value <= (0x3FFF+0x80)) {
			// (0x7F+1) ~ 0x3FFF+(0x7F+1)
			// 0x80 ~ 0x3FFF+0x80
			putShort((short)(0x8000 | (value-0x80)));
		} else if (value>=0 && value <= (0x1FFFFF+0x4080)) {
			// (0x3FFF+(0x7F+1)+1) ~ 0x1FFFFF+(0x3FFF+(0x7F+1)+1)
			// 0x4080 ~ 0x1FFFFF + 0x4080
			put3bytesInt((int)(0xC00000 | (value-0x4080)));
		} else if (value>=0 && value <= (0x0FFFFFFF+0x204080)) {
			// (0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1) ~ 0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)
			// 0x204080 ~ 0x0FFFFFFF + 0x204080
			putInt((int)(0xE0000000 | (value-0x204080)));
		} else if (value>=0 && value <= (0x07FFFFFFFFL+0x10204080)) {
			// (0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1) ~ 0x07FFFFFFFF+(0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1)
			// 0x10204080 ~ 0x07FFFFFFFF+0x10204080
			put5bytesLong(0xF000000000L | (value-0x10204080));
		} else if (value>=0 && value <= (0x03FFFFFFFFFFL+0x0810204080L)) {
			//  (0x07FFFFFFFF+(0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1)) ~ 0x03FFFFFFFFFF+(0x07FFFFFFFF+(0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1))
			// 0x0810204080 ~ 0x03FFFFFFFFFF+0x0810204080
			put6bytesLong(0xF80000000000L | (value-0x0810204080L));
		} else if (value>=0 && value <= (0x01FFFFFFFFFFFFL+0x040810204080L)) {
			// (0x03FFFFFFFFFF+(0x07FFFFFFFF+(0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1))+1) ~ 0x01FFFFFFFFFFFF+(0x03FFFFFFFFFF+(0x07FFFFFFFF+(0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1))+1)
			// 0x040810204080 ~ 0x01FFFFFFFFFFFF+0x040810204080
			put7bytesLong(0xFC000000000000L | (value-0x040810204080L));
		} else if (value>=0 && value <= (0x00FFFFFFFFFFFFFFL+0x02040810204080L)) {
			// (0x01FFFFFFFFFFFF+(0x03FFFFFFFFFF+(0x07FFFFFFFF+(0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1))+1)) ~ 0x00FFFFFFFFFFFFFF+((0x01FFFFFFFFFFFF+(0x03FFFFFFFFFF+(0x07FFFFFFFF+(0x0FFFFFFF+(0x1FFFFF+(0x3FFF+(0x7F+1)+1)+1)+1))+1)))
			// 0x02040810204080 ~ 0x00FFFFFFFFFFFFFF+0x02040810204080
			putLong(0xFE00000000000000L | (value-0x02040810204080L));
		} else {
			// 0x0102040810204080 ~ 0xFFFFFFFFFFFFFFFF
			put((byte)0xFF);
			putLong(value-0x0102040810204080L);
		}
		return this;
	}
	
	public long getVarLong() {
		byte b = get();
		if ((b&0x80)==0) {
			return (b&0x7F);
		} else if ((b&0x40)==0) {
			return (((b&0x3F)<<8) | (get() & 0x00FF))+0x80;
		} else if ((b&0x20)==0) {
			return (((b&0x1F)<<16) | (getShort() & 0x00FFFF))+0x4080;
		} else if ((b&0x10)==0) {
			return (((b&0x0F)<<24) | (get3bytesInt() & 0x00FFFFFF))+0x204080;
		} else if ((b&0x08)==0) {
			return (((long)(b&0x07)<<32) | (getInt() & 0x00FFFFFFFF))+0x10204080;
		} else if ((b&0x04)==0) {
			return (((long)(b&0x03)<<40) | (get5bytesLong() & 0x00FFFFFFFFFFL))+0x0810204080L;
		} else if ((b&0x02)==0) {
			return (((long)(b&0x01)<<48) | (get6bytesLong() & 0x00FFFFFFFFFFFFL))+0x040810204080L;
		} else if ((b&0x01)==0) {
			return (get7bytesLong() & 0x00FFFFFFFFFFFFFFL)+0x02040810204080L;
		} else {
			return getLong()+0x0102040810204080L;
		}
	}
	
	public DynamicByteBuffer putSignedVarLong(long value) {
		putVarLong(((value << 1) ^ (value >> 63)));
		return this;
	}
	
	public long getSignedVarLong() {
		long u= getVarLong();
		return ((u&0x01)==0)?(((u>>1)&0x7FFFFFFFFFFFFFFFL)):((((u>>1)&0x7FFFFFFFFFFFFFFFL)) ^ 0xFFFFFFFFFFFFFFFFL);		
	}
	
	public DynamicByteBuffer putVarLengthData(byte data[]) {
		putVarLong(data.length);
		putLastBytes(data);
		return this;
	}
	
	public DynamicByteBuffer putVarLengthData(ByteBuffer buffer) {
		if (_position<0) {
			throw new IndexOutOfBoundsException();
		}
		
		int l = buffer.remaining();
		putVarLong(l);
		
		if (_position+l>_limit) {
			limit(_position+l);
		}
		
		if (l>0) {
			buffer.get(_data, _position, l);
			_position += l;			
		}
		
		return this;
	}
	
	public byte[] getVarLengthData() {
		long length = getVarLong();
		return getBytesWithFixedLength((int)length);
	}
	
	public DynamicByteBuffer putVarLengthString(String s) {
		byte data[];
		try {
			data = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING, "Not supported encoded", e);
			data = s.getBytes();
		}
		
		putVarLong(data.length);
		putLastBytes(data);
		
		return this;
	}
	
	public String getVarLengthString() {
		int len = (int)getVarLong();
		
		return getStringWithFixedLength(len);
	}
	
	/// 1 byte crc and xor
	public DynamicByteBuffer put1ByteCRC() {
		byte crc=0;
		for (int i=0; i<_position; i++) {
			if ((i&0x0f)==0) {
				crc = (byte) ~crc;
			}
			crc ^= _data[i];
		}
		
		return this.put(crc);
	}	

	public DynamicByteBuffer applyXOR() {
		byte xor = (byte) 0xff;
		for (int i=_position-1; i>=0; i--) {
			xor ^= _data[i];
			_data[i] = xor;
		}
		
		return this;
	}
	
	public DynamicByteBuffer applyXOR2() {
		byte xor = (byte) 0x5a;
		for (int i=_position-1; i>=0; i--) {
			//System.out.printf("%02x ^ %02x", xor, _data[i]);
			xor ^= _data[i];
			//System.out.printf("=%02X\n", xor);
			_data[i] = xor;
		}
		
		return this;
	}
	
	public DynamicByteBuffer applyXOR3() {
		byte xor = (byte) 0x5a;
		for (int i=_position-1; i>=0; i-=3) {
			//System.out.printf("%02x ^ %02x", xor, _data[i]);
			//xor = (byte)(xor ^ _data[i]);
			xor ^= _data[i];
			//System.out.printf("=%02X\n", xor);
			_data[i] = xor;
		}
		for (int i=_position-2; i>=0; i-=3) {
			//System.out.printf("%02x ^ %02x", xor, _data[i]);
			//xor = (byte)(xor ^ _data[i]);
			xor ^= _data[i];
			//System.out.printf("=%02X\n", xor);
			_data[i] = xor;
		}
		for (int i=_position-3; i>=0; i-=3) {
			//System.out.printf("%02x ^ %02x", xor, _data[i]);
			//xor = (byte)(xor ^ _data[i]);
			xor ^= _data[i];
			//System.out.printf("=%02X\n", xor);
			_data[i] = xor;
		}
		
		return this;
	}

	

		// if use, always call this after init.
	public boolean truncate1ByteCRC() {
		switch (_limit) {
		case 0:
			return false;
		default: {
			byte crc=0;
			for (int i=0; i<_limit-1; i++) {
				if ((i&0x0f)==0) {
					crc = (byte) ~crc;
				}
				crc ^= _data[i];
			}
			if (_data[_limit-1]==crc) {
				_limit--;
				return true;
			} else {
				return false;
			}
		}
		}
		//return true;
	}

	public DynamicByteBuffer unapplyXOR()
	{
		byte xor=(byte) 0xff,pre=0;
		for (int i=_limit-1; i>=0; i--) {
			pre = _data[i];
			_data[i] ^= xor;
			xor = pre;
		}
		
		return this;
	}	
	
	public DynamicByteBuffer unapplyXOR2()
	{
		byte xor=(byte) 0x5a,pre=0;
		for (int i=_limit-1; i>=0; i--) {
			pre = _data[i];
			_data[i] ^= xor;
			xor = pre;
		}
		
		return this;
	}
	
	
	public static byte[] readAllBytes(InputStream is) {

	    ByteArrayOutputStream ous = null;
	    try {
	        byte[] buffer = new byte[1024];
	        ous = new ByteArrayOutputStream();
	        int read = 0;
	        try {
				while ( (read = is.read(buffer)) > 0 ) {
				    ous.write(buffer, 0, read);
				}
			} catch (Exception e) {
				return null;
			}
	    } finally { 
	        try {
	        	if (ous!=null) ous.close();
	        } catch (Exception e) {
	        	return null;
	        }

	        try {
	             if (is!= null ) is.close();
	        } catch ( Exception e) {
	        	return null;
	        }
	    }
	    return ous.toByteArray();
	}
	
	public static String readAllBytesAsString(InputStream is) {
		byte bs[] = readAllBytes(is);
		if (bs==null) return null;
		try {
			return new String(bs, "UTF-8");
		} catch(Exception e) {
			return new String(bs);
		}
	}
}



