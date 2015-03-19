import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public abstract class InitCipher {
	
	private Cipher c;
	
	public Cipher initCipherByType(String type, int opmode, byte[] key, byte[] iv){
		
		SecretKeySpec finalKey;
		byte[] key128 = null;
		try {	
			
			//Type RC4
			if(type.equals("RC4")){
				MessageDigest hasher = MessageDigest.getInstance("MD5"); // Initialize object that will hash my key.
	            key128 = hasher.digest(key); // Hash the key to 128 bits using MD5
	            finalKey = new SecretKeySpec( key128, "RC4");
				
				c = Cipher.getInstance("RC4");
				c.init(opmode, finalKey);
			}
			
			//Type AES/CBC/NoPadding
			else if(type.equals("AES/CBC/NoPadding")){
				MessageDigest hasher = MessageDigest.getInstance("MD5"); // Initialize object that will hash my key.
	            key128 = hasher.digest(key); // Hash the key to 128 bits using MD5
				SecretKeySpec finalkKey = new SecretKeySpec( key128, "AES");
				
				c = Cipher.getInstance("AES/CBC/NoPadding");
				c.init(opmode, finalkKey, new IvParameterSpec(iv));
				 
			}
			
			//Type AES/CBC/PKCS5Padding
			else if(type.equals("AES/CBC/PKCS5Padding")){
				MessageDigest hasher = MessageDigest.getInstance("MD5"); // Initialize object that will hash my key.
	            key128 = hasher.digest(key); // Hash the key to 128 bits using MD5
				finalKey = new SecretKeySpec( key128, "AES");
				
				c = Cipher.getInstance("AES/CBC/PKCS5Padding");
				c.init(opmode, finalKey, new IvParameterSpec(iv));
			}
			
			//Type AES/CFB8/PKCS5Padding
			else if(type.equals("AES/CFB8/PKCS5Padding")){
				MessageDigest hasher = MessageDigest.getInstance("MD5"); // Initialize object that will hash my key.
	            key128 = hasher.digest(key); // Hash the key to 128 bits using MD5
				finalKey = new SecretKeySpec( key128, "AES");
				
				c = Cipher.getInstance("AES/CBC/PKCS5Padding");
				c.init(opmode, finalKey, new IvParameterSpec(iv));
			}
			/*
			//Type AES/CFB8/NoPadding
			else if(type.equals("AES/CFB8/NoPadding")){
				SecretKeySpec sks = new SecretKeySpec( key, "AES");
				c = Cipher.getInstance("AES/CFB8/NoPadding");
			}
			
			//Type AES/CFB/NoPadding
			else if(type.equals("AES/CFB/NoPadding")){
				SecretKeySpec sks = new SecretKeySpec( key, "AES");
				c = Cipher.getInstance("AES/CFB/NoPadding");
			}
			
			*/
			
			
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("SessionKey Size: " + key.length);
		System.out.println("HashKey Size: " + key128.length);
		return c;
	}
}
