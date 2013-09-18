package cc.waynes.crypto;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

import javax.crypto.Cipher;

import cc.waynes.util.MemoryPrinter;

public class CCipher_RSAPublicKey extends CCipher {
	Key _publicKey;
	
	public CCipher_RSAPublicKey(byte bytes_in_X509EncodedKeySpec[]) throws NoSuchAlgorithmException, InvalidKeySpecException {
		MemoryPrinter.printMemory(log, Level.INFO, "RSA public key", bytes_in_X509EncodedKeySpec, 0, bytes_in_X509EncodedKeySpec.length, 0);
		
		KeyFactory fact = KeyFactory.getInstance("RSA");
		_publicKey = fact.generatePublic(new X509EncodedKeySpec(bytes_in_X509EncodedKeySpec));
	}
	
	public CCipher_RSAPublicKey(Key publicKey) {
		_publicKey = publicKey;
	}

	@Override
	public byte[] encrypt(byte plaindata[], int offset, int length) {
		String transformation = "RSA/ECB/PKCS1Padding";  // RSA/ECB/PKCS1Padding, RSA/ECB/NOPadding
		
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, _publicKey);
			int blockSize = 128 - 11;
			
			return super.encrypt(cipher, blockSize, plaindata, offset, length);
			//return cipher.doFinal(plaindata, offset, length);
		} catch (Exception e) {
			log.log(Level.WARNING, "RSAPublicKey encrypt", e);
		}
		
		return null;
	}

	@Override
	public byte[] decrypt(byte cipherdata[], int offset, int length) {
		String transformation = "RSA/ECB/PKCS1Padding";  // RSA/ECB/PKCS1Padding, RSA/ECB/NOPadding
		
		Cipher cipher;
		try {
			
			cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, _publicKey);
			int blockSize = 128;//cipher.getBlockSize();
			
			return super.decrypt(cipher, blockSize, cipherdata, offset, length);
			
		} catch (Exception e) {
			log.log(Level.WARNING, "RSAPublicKey decrypt", e);
		}
		
		return null;
	}

}











