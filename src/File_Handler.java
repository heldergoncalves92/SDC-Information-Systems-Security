import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
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
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException{
		int opt = 0;
		File_Handler fh = new File_Handler();
		
		
		if(opt == 0) fh.genKey("keyfile.txt");
		
	}
	
	
}
