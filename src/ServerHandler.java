
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.Cipher;


public class ServerHandler extends InitCipher implements Runnable {

	private int id;
	private Socket socket;
	private Message_Handler handler;
	private BufferedWriter file;
	
	private byte[] iv = "1234567812345678".getBytes();
	
	public ServerHandler(int num, Socket s, BufferedWriter file, Server server){
		
		this.id=num;
		this.socket=s;
		this.file = file;
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
		String type = "AES/CBC/NoPadding";	
		c = initCipherByType(type, Cipher.DECRYPT_MODE, key, iv);
		
		//Prepare Message_Handler with Cipher
		handler.setCipher(c);
		handler.setMac(key);
	}
	
	
	public void run() {
		String recv;
		try{
			while(true){
				recv = handler.receiveMessage();
				
				//Reserved words
				if(recv.equals("Out")) break;
				//else if(recv.equals("Shutdown")){ server.shutdown(); break;}
				
				//Write on file
				file.write(id+": "+ recv + "\n");
				file.flush();
				
				//Write on screen
				System.out.println(recv + recv.length());
            }
				
			//Write on file
			file.write("=["+ id +"]=\n");
			file.flush();
			
			//Write on screen
			System.out.println("=["+ id +"]=\n");
			
			socket.close();
		}catch(IOException e){}
	}
	
	public void closeConn() throws IOException{socket.close();}
	
}
