import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class Client{
	
	private byte[] key = "HELLO cripto!!".getBytes();
	private boolean bool = true;
	
	public Client(){}
	
	public void runClient(){
		/*	String host = args[0];
		int port = Integer.parseInt(args[1]);
		Socket s = new Socket(host, port);
		*/String msg = "";
		
		Socket s;
		try {
			s = new Socket("localhost", 	6000);
		
			BufferedReader sockIn = new BufferedReader( new InputStreamReader(System.in));
			BufferedWriter sockOut = new BufferedWriter( new OutputStreamWriter(s.getOutputStream()));
			
			//SecretKeySpec sks = new SecretKeySpec( key, "RC4");
			//Cipher c = Cipher.getInstance("RC4");
			
			SecretKeySpec sks = new SecretKeySpec( key, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE , sks);
			
			CipherOutputStream cos = new CipherOutputStream(s.getOutputStream(), c);
			int test;
			
			while(bool){
				
				while((test = System.in.read()) != -1){
					cos.write((byte)test);
					cos.flush();
				}
				
				/*
				msg = sockIn.readLine();
				sockOut.write(msg);
				sockOut.flush();
				if(msg.equals("Sair")  || msg.equals("Shutsown")) break; 
					*/
			}
			s.close();
		} catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		Client cli = new Client();
		cli.runClient();
	
	}
	
	
	
}
