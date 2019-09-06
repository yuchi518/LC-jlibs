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
import java.io.InputStream;

public class InputStreamByteBuffer extends DynamicByteBuffer {
	
	final protected InputStream inputStream;
	long _count = 0;
	
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
	public int position() {
		long l = positionL();
		return Math.toIntExact(l);
	}

	public long positionL() { return _count - (_limit - _position); }
	
	@Override
	public DynamicByteBuffer position(int newPosition) { throw new UnsupportedOperationException(); }
	
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
					_count += rL;
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
				_count += rL;
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







