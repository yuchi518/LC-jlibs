package me.tuple.crypto;

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
			log.log(Level.WARNING, "RSAPrivateKey encrypt", e);
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
			log.log(Level.WARNING, "RSAPrivateKey decrypt", e);
		}
		
		return null;
	}

}
