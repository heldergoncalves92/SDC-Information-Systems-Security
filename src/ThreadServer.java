
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.Cipher;


public class ThreadServer extends InitCipher implements Runnable {

	private int id;
	private Socket socket;
	private Message_Handler handler;
	
	private byte[] iv = "1234567812345678".getBytes();
	private boolean bool=true;
	
	
	public ThreadServer(int num, Socket s, BufferedWriter file, Server server){
		
		this.id=num;
		this.socket=s;
		PublicKey publicKey;
		Cipher c;
		
		//To accord the Diffie-Hellman Key
		DiffieHellman df = new DiffieHellman();
		KeyPair kPair = df.generateKey();
			
		//Init Message_Handler and Get PublicKey
		handler = new Message_Handler(s);
		publicKey = handler.SendRecvKey(kPair.getPublic());
		
		//Generate SessionKey
		byte[] key = df.sessionKey(kPair.getPrivate(), publicKey);
		
		//Init Cipher
		String type = "AES/CFB8/PKCS5Padding";	
		c = initCipherByType(type, Cipher.DECRYPT_MODE, key, iv);
		
		//Prepare Message_Handler with Cipher
		handler.setCipher(c);
		handler.setMac(key);
	}
	
	
	public void run() {
		String recv;
		try{
			while(bool){
				
				recv = handler.receiveMessage();
				System.out.println(recv);
				
				/*
				if(str.equals("Sair")) break;
				else if(str.equals("Shutdown")){ server.shutdown(); break;}
				
				out.write(id+": "+ str + "\n");
				out.flush();
				*/
            }
				
			System.out.println("=["+ id +"]=\n");
			
			socket.close();
		}catch(IOException e){}
	}
	
	public void closeConn() throws IOException{socket.close();}
	
}
