/*
 * LC-jlibs, lets.cool java libraries
 * Copyright (C) 2015-2018 Yuchi Chen (yuchi518@gmail.com)

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation. For the terms of this
 * license, see <http://www.gnu.org/licenses>.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package lets.cool.util;

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
			if (_position>0) outputStream.write(_data, 0, _position);
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













