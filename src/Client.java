import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;



public class Client extends InitCipher{
	
	private byte[] key;
	private byte[] iv = "1234567812345678".getBytes();
	
	private boolean bool = true;
	
	public Client(){}
	
	public void runClient(){
		Cipher c;
		String msg = "";
		int i, offset;
		int blockDim = 16;
		byte[] toEncrypt;
		byte[] endLine = new byte[16];
		endLine[0]=(byte)'\n';
		
		Socket s;
		
		try {
			s = new Socket("localhost", 6000);
		
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
			
			c = initCipherByType(type, Cipher.ENCRYPT_MODE, key, iv);
			
			CipherOutputStream cos = new CipherOutputStream(s.getOutputStream(), c);
			BufferedReader in = new BufferedReader( new InputStreamReader(System.in));
			
			while(bool){
				
				msg = in.readLine();
				System.out.println(msg.length());
				
				toEncrypt = msg.getBytes();
				for(i=0; (offset=i*blockDim) < msg.length(); i++){
					if(msg.length()-offset >= blockDim){
						cos.write(toEncrypt, offset, blockDim);
						cos.flush();
					}else{
						byte[] padding = new byte[blockDim];
						System.arraycopy(toEncrypt, offset, padding, 0, msg.length()-offset);
						cos.write(padding);
						cos.flush();
					}	
				}
				cos.write(endLine);
				cos.flush();
				
				
				/*
				sockOut.write(msg);
				sockOut.flush();
				if(msg.equals("Sair")  || msg.equals("Shutdown")) break; 
				 */		
			}
			cos.close();
			s.close();
			} catch (IOException   e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	public static void main(String[] args) {
		Client cli = new Client();
		cli.runClient();
	
	}
	
	
	
}
