package me.tuple.util;

import java.io.IOException;
import java.io.InputStream;

public class PositionInputStream extends InputStream {

	protected InputStream is;
	protected long position;
	protected long markPosition;
	
	public PositionInputStream(InputStream input) {
		is = input;
		position = 0;
	}
	
	public long position() {
		return position;
	}

	@Override
	public int read() throws IOException {
		int i = is.read();
		position += (i<0)?0:1;
		return i;
	}
	
	@Override
	public int read(byte b[]) throws IOException {
		int i = is.read(b);
		position += (i<0)?0:i;
		return i;
	}
	
	@Override
	public int read(byte b[], int off, int len) throws IOException {
		int i = is.read(b, off, len);
		position += (i<0)?0:i;
		return i;
	}
	
	@Override
	public long skip(long n) throws IOException {
		long s = is.skip(n);
		position += s;
		return s;
	}
	
	@Override
	public int available() throws IOException {
		return is.available();
	}
	
	@Override
	public void close() throws IOException {
		is.close();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		is.mark(readlimit);
		markPosition = position;
	}
	
	@Override
	public synchronized void reset() throws IOException {
		is.reset();
		position = markPosition;
	}
	
	@Override
	public boolean markSupported() {
		return is.markSupported();
	}
	
	
	
}



















