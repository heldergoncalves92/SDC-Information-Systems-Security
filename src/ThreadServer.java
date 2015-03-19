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
	
	private byte[] key ;
	private byte[] iv = "1234567812345678".getBytes();
	private Cipher c;
	private CipherInputStream cis;
	
	
	public ThreadServer(int num, Socket s, BufferedWriter file, Server server){
		
		this.id=num;
		this.socket=s;
		
		try{	
			
			//To accord the Diffie-Hellman Key
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			
			DiffieHellman df = new DiffieHellman();
			KeyPair kPair = df.generateKey();
			
			oos.writeObject(kPair.getPublic());
			oos.flush();
			PublicKey publicKey = (PublicKey)ois.readObject();
			
			key = df.sessionKey(kPair.getPrivate(), publicKey);
			
			//Init Cipher
			String type = "AES/CFB8/PKCS5Padding";
			c = initCipherByType(type, Cipher.DECRYPT_MODE, key, iv);
			this.cis = new CipherInputStream(s.getInputStream(), c);
			
		}catch(IOException | ClassNotFoundException   e){
			System.out.println("ERROR: Creating CipherInputStream!!\n");
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
