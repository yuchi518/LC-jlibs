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



















