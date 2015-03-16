import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public abstract class InitCipher {
	
	private Cipher c;
	
	public Cipher initCipherByType(String type, int opmode, byte[] key, byte[] iv){
		try {	
			
			//Type RC4
			if(type.equals("RC4")){
				SecretKeySpec sks = new SecretKeySpec( key, "RC4");
				c = Cipher.getInstance("RC4");
				c.init(opmode, sks);
			}
			
			//Type AES/CBC/NoPadding
			else if(type.equals("AES/CBC/NoPadding")){
				SecretKeySpec aesKey = new SecretKeySpec( key, "AES");
				c = Cipher.getInstance("AES/CBC/NoPadding");
				c.init(opmode, aesKey, new IvParameterSpec(iv));
				 
			}
			
			//Type AES/CBC/PKCS5Padding
			else if(type.equals("AES/CBC/PKCS5Padding")){
				SecretKeySpec aesKey = new SecretKeySpec( key, "AES");
				c = Cipher.getInstance("AES/CBC/PKCS5Padding");
				c.init(opmode, aesKey, new IvParameterSpec(iv));
			}
			
			//Type AES/CFB8/PKCS5Padding
			else if(type.equals("AES/CFB8/PKCS5Padding")){
				SecretKeySpec aesKey = new SecretKeySpec( key, "AES");
				c = Cipher.getInstance("AES/CBC/PKCS5Padding");
				c.init(opmode, aesKey, new IvParameterSpec(iv));
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
		
		return c;
	}
}
