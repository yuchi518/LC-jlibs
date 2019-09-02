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

import lets.cool.util.logging.Logr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class MappedByteBuffer {

	final protected static Logr log = Logr.logger();
	
	final int BUFFER_SIZE;
	final int PAGE_SIZE;
	
	//protected File file;
	protected RandomAccessFile raFile;
	protected DynamicByteBuffer buffer;
	
	private long _position=-1;
	
	/** 1k pageSize, 4k bufferSize */
	public MappedByteBuffer(File file) {
		this(file, 1024*4, 1024*1);
	}
	
	/** 1k pageSize */
	public MappedByteBuffer(File file, int bufferSize) {
		this(file, bufferSize, 1024*1);
	}
	
	/**
	 *  pageSize is the minimum data size for writing to or reading from file.
	 *  bufferSize is the memory size to maintenance file data.
	 * @throws FileNotFoundException 
	 *  */
	public MappedByteBuffer(File file, int bufferSize, int pageSize) {
		//this.file = file;
		try {
			raFile = new RandomAccessFile(file, "rwd");
		} catch (FileNotFoundException e) {
			log.error("File can't access.", e);
			throw new RuntimeException(e);
		}
		
		this.PAGE_SIZE = pageSize;
		
		this.BUFFER_SIZE = (bufferSize%pageSize==0)?bufferSize:((bufferSize/pageSize)+1)*pageSize;
		
		buffer = new DynamicByteBuffer(this.BUFFER_SIZE);
		buffer.fixedCapacity = true;
	}
	
	
	public MappedByteBuffer position(long position) {
		if (_position<0) {
			_position = position;
			buffer.clear();
			
			
		} else {
			// check need to flush data or not
		}
		
		
		
		return this;
	}
	
	public long position() {
		return _position;
	}
	
	public void flush() {
		if (_position > 0) {
			
		}
	}
	
	
}







