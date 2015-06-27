import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class File_Handler {

	
	private byte[] byteKey = "Hello Cripto!!".getBytes();
	private final int stride = 2;
	
	
	public void genKey(String file) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException{
		
		//Init File OutputStream and write the Key
		FileOutputStream out =  new FileOutputStream(file);
		out.write(byteKey);
		out.close();
	}
	
	public void encrypt(){
		
		//String for tests
		String byteString ="Hello, everything ok?? Good, you broke the RC4 cipher!!!";
		int i=0, length = byteString.length();
		int offset;
		byte[] encrypted;
		
		try{
			SecretKeySpec sks = new SecretKeySpec(byteKey, "RC4");
			Cipher c = Cipher.getInstance("RC4");
			c.init(Cipher.ENCRYPT_MODE , sks);
			
			FileOutputStream out =  new FileOutputStream("encrypt.txt");
			
			while((offset = i*stride) < length){
				if(offset +stride >length){
					encrypted= c.doFinal(byteString.getBytes(),offset,length-offset);
					out.write(encrypted,0,length-offset);
				}
				else{
					encrypted= c.doFinal(byteString.getBytes(),offset,stride);
					out.write(encrypted,0,stride);
				}
				out.flush();
				i++;
			}
			out.close();
		}catch(IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){}
	}
	
	public void decrypt(){
	
		int length;
		byte[] toDecrypt = new byte[stride];
		
		try{
			FileInputStream in = new FileInputStream("encrypt.txt");
			FileOutputStream out =  new FileOutputStream("decrypt.txt");
			
			SecretKeySpec sks = new SecretKeySpec(byteKey, "RC4");
			Cipher c = Cipher.getInstance("RC4");
			c.init(Cipher.DECRYPT_MODE , sks);
			
			length = in.read(toDecrypt, 0, stride);
			while(length != -1){ 
				out.write(c.doFinal(toDecrypt, 0, length));
				out.flush();
				length = in.read(toDecrypt, 0, stride);
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
