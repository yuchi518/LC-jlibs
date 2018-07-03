package lets.cool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MappedByteBuffer {

	static Logger log  = Logger.getLogger(MappedByteBuffer.class.getName());
	
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
			log.log(Level.SEVERE, "File can't access.", e);
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







