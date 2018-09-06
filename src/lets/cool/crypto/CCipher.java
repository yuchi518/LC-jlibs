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

package lets.cool.crypto;

import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import lets.cool.util.MemoryPrinter;
import lets.cool.util.logging.Level;
import lets.cool.util.logging.Logr;

public abstract class CCipher {
	protected static Logr log = Logr.logger();
	
	public byte[] encrypt(byte plaindata[]) {
		return encrypt(plaindata, 0, plaindata.length);
	}
	
	public abstract byte[] encrypt(byte plaindata[], int offset, int length);
	
	public byte[] decrypt(byte cipherdata[]) {
		return decrypt(cipherdata, 0, cipherdata.length);
	}
	
	public abstract byte[] decrypt(byte cipherdata[], int offset, int length);

	
	protected byte[] encrypt(Cipher cipher, int blockSize, byte input[], int offset, int length) 
			throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		
		
		long dt = System.currentTimeMillis();
		
		MemoryPrinter.printMemory(log, Level.INFO, this.getClass()+" encrypt data", input, offset, length, 0);
		
		//
		/*if ((length%blockSize)!=0) {
			throw new IllegalBlockSizeException("Payload should be " + blockSize + "xN");
		}*/
		
		byte b[] = new byte[(length/blockSize + ((length%blockSize)==0?0:1))*cipher.getOutputSize(blockSize)];
		int pos=0;
		int len=0;
		
		while (len<length) {
			int l = Math.min(length-len, blockSize);
			pos += cipher.doFinal(input, offset+len, l, b, pos);
			len += l;
		}
		
		//pos += cipher.doFinal(b, pos);
		
		
		b = Arrays.copyOf(b, pos);
		
		
		MemoryPrinter.printMemory(log, Level.INFO, this.getClass()+" encrypted data ("+(System.currentTimeMillis()-dt)/1000.0+"s)", b, 0, b.length, 0);
		
		return b;
	}
	
	protected byte[] decrypt(Cipher cipher, int blockSize, byte input[], int offset, int length) 
			throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
		
		long dt = System.currentTimeMillis();
		
		MemoryPrinter.printMemory(log, Level.INFO, this.getClass()+" decrypt data", input, offset, length, 0);
		
		//
		if ((length%blockSize)!=0) {
			throw new IllegalBlockSizeException("Payload should be " + blockSize + "xN");
		}
		
		byte b[] = new byte[length];
		int pos=0;
		
		for (int i=0;i<length/blockSize;i++) {
			pos += cipher.doFinal(input, offset+i*blockSize, blockSize, b, pos);
		}
		
		//pos += cipher.doFinal(b, pos);
		
		
		b = Arrays.copyOf(b, pos);
		
		
		MemoryPrinter.printMemory(log, Level.INFO, this.getClass()+" decrypted data ("+(System.currentTimeMillis()-dt)/1000.0+"s)", b, 0, b.length, 0);
		
		return b;
	}
	
}





