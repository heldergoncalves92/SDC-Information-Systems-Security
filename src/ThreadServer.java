import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class ThreadServer extends InitCipher implements Runnable {

	private Server server;
	private int id;
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	
	private byte[] key = "1234567812345678".getBytes();
	private byte[] iv = "1234567812345678".getBytes();
	private Cipher c;
	private CipherInputStream cis;
	
	
	public ThreadServer(int num, Socket s, BufferedWriter file, Server server){
		try{	
			this.server=server;
			this.id=num;
			this.socket=s;
			this.out = file;
				
			//Init Cipher
			//String type = "AES/CBC/PKCS5Padding";
			String type = "AES/CFB8/PKCS5Padding";
			c = initCipherByType(type, Cipher.DECRYPT_MODE, key, iv);
			
			this.cis = new CipherInputStream(s.getInputStream(), c);
			this.in = new BufferedReader( new InputStreamReader(s.getInputStream()));
			
		}catch(IOException e){
			System.out.println("ERROR: Creating inputStream!!\n");
		}
	}
	
	public void run() {

		try{
			
			System.out.println("Chega While!!");
			int test;
			while ((test=cis.read()) != -1) {
               	//System.out.println("***"+test+"***");
				System.out.print((char) test);
				
				/*
				str = in.readLine();
				if(str.equals("Sair")) break;
				else if(str.equals("Shutdown")){ server.shutdown(); break;}
				
				out.write(id+": "+ str + "\n");
				out.flush();
				*/
			
            }
				
			System.out.println("=["+ id +"]=\n");
			
			in.close();
			socket.close();
		}catch(IOException e){}
	}
	
	public void closeConn() throws IOException{socket.close();}
	
}
