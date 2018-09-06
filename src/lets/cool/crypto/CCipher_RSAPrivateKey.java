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

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Level;

import javax.crypto.Cipher;


public class CCipher_RSAPrivateKey extends CCipher {
	Key _privateKey;
	
	public CCipher_RSAPrivateKey(byte bytes_in_PKCS8EncodedKeySpec[]) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory fact = KeyFactory.getInstance("RSA");
		_privateKey = fact.generatePrivate(new PKCS8EncodedKeySpec(bytes_in_PKCS8EncodedKeySpec));
		
	}
	
	public CCipher_RSAPrivateKey(Key privateKey) {
		_privateKey = privateKey;
	}

	@Override
	public byte[] encrypt(byte plaindata[], int offset, int length) {
		String transformation = "RSA/ECB/PKCS1Padding";  // RSA/ECB/PKCS1Padding, RSA/ECB/NOPadding
		
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, _privateKey);
			int blockSize = 128 - 11;
			
			return super.encrypt(cipher, blockSize, plaindata, offset, length);
			//return cipher.doFinal(plaindata, offset, length);
		} catch (Exception e) {
			log.warn("RSAPrivateKey encrypt", e);
		}
		
		return null;
	}

	@Override
	public byte[] decrypt(byte cipherdata[], int offset, int length) {
		String transformation = "RSA/ECB/PKCS1Padding";  // RSA/ECB/PKCS1Padding, RSA/ECB/NOPadding
		
		Cipher cipher;
		try {
			
			cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, _privateKey);
			int blockSize = 128;//cipher.getBlockSize();
			
			return super.decrypt(cipher, blockSize, cipherdata, offset, length);
			
		} catch (Exception e) {
			log.warn("RSAPrivateKey decrypt", e);
		}
		
		return null;
	}

}
