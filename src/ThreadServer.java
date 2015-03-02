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


public class ThreadServer implements Runnable {

	private Server server;
	private int id;
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private boolean bool = true;
	
	private byte[] key = "HELLO cripto!!".getBytes();
	private byte[] iv = "HELLO cripto!!".getBytes();
	private Cipher c;
	private CipherInputStream cis;
	
	public void getCipherByType(String type){
		try {
			
			//Type RC4
			if(type.equals("RC4")){
				SecretKeySpec sks = new SecretKeySpec( key, "RC4");
				this.c = Cipher.getInstance("RC4");
			}
			
			//Type AES/CBC/NoPadding
			else if(type.equals("AES/CBC/NoPadding")){
				SecretKeySpec aesKey = new SecretKeySpec( key, "AES");
				this.c = Cipher.getInstance("AES/CBC/NoPadding");
				c.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
				 
			}
			
			//Type AES/CBC/PKCS5Padding
			else if(type.equals("AES/CBC/PKCS5Padding")){
				SecretKeySpec aesKey = new SecretKeySpec( key, "AES");
				this.c = Cipher.getInstance("AES/CBC/PKCS5Padding");
				c.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
			}
			/*
			//Type AES/CFB8/PKCS5Padding
			else if(type.equals("AES/CFB8/PKCS5Padding")){
				SecretKeySpec sks = new SecretKeySpec( key, "AES");
				this.c = Cipher.getInstance("AES/CFB8/PKCS5Padding");
			}
			
			//Type AES/CFB8/NoPadding
			else if(type.equals("AES/CFB8/NoPadding")){
				SecretKeySpec sks = new SecretKeySpec( key, "AES");
				this.c = Cipher.getInstance("AES/CFB8/NoPadding");
			}
			
			//Type AES/CFB/NoPadding
			else if(type.equals("AES/CFB/NoPadding")){
				SecretKeySpec sks = new SecretKeySpec( key, "AES");
				this.c = Cipher.getInstance("AES/CFB/NoPadding");
			}
			
			*/
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public ThreadServer(int num, Socket s, BufferedWriter file, Server server){
		this.server=server;
		this.id=num;
		this.socket=s;
		try{
			this.in = new BufferedReader( new InputStreamReader(s.getInputStream()));
			
			getCipherByType("RC4");
			
			this.cis = new CipherInputStream(s.getInputStream(), c);
		
		}catch(IOException e){
			System.out.println("ERROR: Creating inputStream!!\n");
			}
		this.out = file;
	}
	
	public void run() {
		String str="";
		
		try{
			while(bool){
				
				int test;
                while ((test=cis.read()) != -1) {
                      System.out.print((char) test);
                }
				
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
