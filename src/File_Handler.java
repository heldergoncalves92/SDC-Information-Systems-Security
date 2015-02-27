import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class File_Handler {

	
	private byte[] byteKey = "Hello Cripto!!".getBytes();
	
	
	public void genKey(String file) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException{
		
		//Init File OutputStream and write the Key
		FileOutputStream out =  new FileOutputStream(file);
		out.write(byteKey);
		out.close();
	}
	
	public void encrypt(){
		
		String string ="Hello, everything ok?? Good, you broke the RC4 cipher!!";
		
		try{
			SecretKeySpec sks = new SecretKeySpec(byteKey, "RC4");
			Cipher c = Cipher.getInstance("RC4");
			c.init(Cipher.ENCRYPT_MODE , sks);
			
			FileOutputStream out =  new FileOutputStream("encrypt.txt");
			
			byte[] encrypted= c.doFinal(string.getBytes());
			
			out.write(encrypted);
			out.flush();
			out.close();
		}catch(IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){}
		
	}
	
	public void decrypt(){
	
		try{
			FileInputStream in = new FileInputStream("encrypt.txt");
			FileOutputStream out =  new FileOutputStream("decrypt.txt");
			
			SecretKeySpec sks = new SecretKeySpec(byteKey, "RC4");
			Cipher c = Cipher.getInstance("RC4");
			c.init(Cipher.DECRYPT_MODE , sks);
		
			byte[] toDecrypt = new byte[32];

			while((in.read(toDecrypt) ) != -1){ 
				out.write(c.doFinal(toDecrypt));
				out.flush();
			}
			
			out.close();
			in.close();
			
		}catch(IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){}
		
	}
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException{
		int opt = 2; 
		File_Handler fh = new File_Handler();
		
		
		if(opt == 0) fh.genKey("keyfile.txt");
		else if(opt == 1) fh.encrypt();
		else if(opt == 2) fh.decrypt();
		
	}
	
	
}
