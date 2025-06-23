package utils;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

	public static String generateSalt() {
		byte[] salt = new byte[16];
		new SecureRandom().nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	public static String hashPassword(String password, String salt) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65536, 256);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] hash = factory.generateSecret(spec).getEncoded();
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("A mistake happened when hashing the password", e);
		}
	}

	public static boolean verifyPassword(String enteredPassword, String storedHash, String storedSalt) {
		String newHash = hashPassword(enteredPassword, storedSalt);
		return newHash.equals(storedHash);
	}

}
