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

import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lets.cool.util.MemoryPrinter;

public class CCipher_AESKey extends CCipher {
	SecretKeySpec _spec;
	IvParameterSpec _iv;
	
	public CCipher_AESKey(byte keySpec[], byte iv[]) {
		_spec = new SecretKeySpec(keySpec, "AES");
		_iv = new IvParameterSpec(iv, 0, 16);
	}

	@Override
	public byte[] encrypt(byte[] plaindata, int offset, int length) {
		try {
			long dt = System.currentTimeMillis();
			
			MemoryPrinter.printMemory(log, Level.INFO, "AES encrypt data", plaindata, offset, length, 0);
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			//Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, _spec, _iv);
			byte b[] = cipher.doFinal(plaindata, offset, length);
			
			MemoryPrinter.printMemory(log, Level.INFO, "AES encrypted data ("+(System.currentTimeMillis()-dt)/1000.0+"s)", b, 0, b.length, 0);
			
			return b;
		} catch (Exception e) {
			log.log(Level.WARNING, "CCipher_AESKey encrypt", e);
		}
		return null;
	}

	@Override
	public byte[] decrypt(byte[] cipherdata, int offset, int length) {
		try {
			long dt = System.currentTimeMillis();
			
			MemoryPrinter.printMemory(log, Level.INFO, "AES descrypt data", cipherdata, offset, length, 0);
			
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, _spec, _iv);
			byte b[] = cipher.doFinal(cipherdata, offset, length);

			MemoryPrinter.printMemory(log, Level.INFO, "AES descrypted data ("+(System.currentTimeMillis()-dt)/1000.0+"s)", b, 0, b.length, 0);
			
			return b;
		} catch (Exception e) {
			log.log(Level.WARNING, "CCipher_AESKey decrypt", e);
		}
		return null;
	}

}
