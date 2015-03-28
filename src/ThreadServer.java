import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;


public class ThreadServer extends InitCipher implements Runnable {

	private int id;
	private Socket socket;
	private BufferedReader in;
	private Message_Handler handler;
	
	private byte[] key ;
	private byte[] iv = "1234567812345678".getBytes();
	private Cipher c;
	private boolean bool=true;
	
	
	public ThreadServer(int num, Socket s, BufferedWriter file, Server server){
		
		this.id=num;
		this.socket=s;
		PublicKey publicKey;
		
		//To accord the Diffie-Hellman Key
		DiffieHellman df = new DiffieHellman();
		KeyPair kPair = df.generateKey();
			
		//Init Message_Handler and Get PublicKey
		handler = new Message_Handler(s);
		publicKey = handler.SendRecvKey(kPair.getPublic());
		
		//Generate SessionKey
		key = df.sessionKey(kPair.getPrivate(), publicKey);
		
		//Init Cipher
		String type = "AES/CFB8/PKCS5Padding";	
		c = initCipherByType(type, Cipher.DECRYPT_MODE, key, iv);
		
		//Prepare Message_Handler with Cipher
		handler.setCipher(c);
	}
	
	
	public void run() {
		String recv;
		try{
			while(bool){
				
				recv = handler.receiveMessage();
				System.out.println(recv);
				
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
