package me.tuple.crypto;

import java.util.logging.Level;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.tuple.util.MemoryPrinter;

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
