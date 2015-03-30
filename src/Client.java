import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.Cipher;



public class Client extends InitCipher{
	
	private byte[] key;
	private byte[] iv = "1234567812345678".getBytes();
	
	
	private boolean bool = true;
	private Message_Handler handler;
	private BufferedReader in;
	
	public Client(){}
	
	public void runClient(){
		Cipher c;
		String msg = "";
		
		PublicKey publicKey;
		Socket s;
		
		try {
			s = new Socket("localhost", 6000);
		
			//To accord the Diffie-Hellman Key
			DiffieHellman df = new DiffieHellman();
			KeyPair kPair = df.generateKey();
			
			//Init Message_Handler and Get PublicKey
			handler = new Message_Handler(s);
			publicKey = handler.SendRecvKey(kPair.getPublic());
			
			//Generate SessionKey
			byte[] key = df.sessionKey(kPair.getPrivate(), publicKey);
		
			//Init Cipher
			String type = "AES/CBC/NoPadding";	
			c = initCipherByType(type, Cipher.ENCRYPT_MODE, key, iv);
			
			//Prepare Message_Handler with Cipher
			handler.setCipher(c);
			handler.setMac(key);
			in = new BufferedReader(new InputStreamReader(System.in));
			
			while(bool){
				msg = in.readLine();
				handler.sendMessage(msg);
				
				/*
				sockOut.write(msg);
				sockOut.flush();
				if(msg.equals("Sair")  || msg.equals("Shutdown")) break; 
				 */		
			}
			s.close();
			} catch (IOException   e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	public static void main(String[] args) {
		Client cli = new Client();
		cli.runClient();
	}
	
	
	
}
