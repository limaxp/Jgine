package org.jgine.misc.utils.encryption;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jgine.misc.utils.logger.Logger;

public class EncryptionAsymetric {

	private static final String ALGORITHM = "RSA";
	private static final int KEY_SIZE = 2048;
	private static final Cipher CIPHER;

	static {
		try {
			CIPHER = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			Logger.err("EncryptionAsymetric: Error initializing Cipher!", e);
			throw new RuntimeException(e);
		}
	}

	public static byte[] encrypt(byte[] data, Key key) {
		byte[] encrypted;
		try {
			CIPHER.init(Cipher.ENCRYPT_MODE, key);
			encrypted = CIPHER.doFinal(data);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			Logger.err("EncryptionAsymetric: Error encrypting input!", e);
			return null;
		}
		return base64Encode(encrypted);
	}

	public static byte[] decrypt(byte[] data, Key key) {
		data = base64Decode(data);
		try {
			CIPHER.init(Cipher.DECRYPT_MODE, key);
			return CIPHER.doFinal(data);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			Logger.err("EncryptionAsymetric: Error decrypting input!", e);
			return null;
		}
	}

	public static KeyPair generateKey() {
		SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			Logger.err("EncryptionAsymetric: Error getting SecureRandom instance", e);
			return null;
		}
		KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
			keyPairGenerator.initialize(KEY_SIZE, secureRandom);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			Logger.err("EncryptionAsymetric: Error generating key pair for algorithm " + ALGORITHM, e);
			return null;
		}
	}

	private static byte[] base64Encode(byte[] data) {
		return Base64.getEncoder().encode(data);
	}

	private static byte[] base64Decode(byte[] data) {
		return Base64.getDecoder().decode(data);
	}
}
