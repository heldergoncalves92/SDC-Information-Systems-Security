import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class Message_Handler {
	
	private Cipher cipher;
	private Mac macHandler;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public Message_Handler(Socket s){
		try {
			this.out = new ObjectOutputStream(s.getOutputStream());
			this.in = new ObjectInputStream(s.getInputStream());
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Message_Handler(Socket s, Cipher c){
		try {
			this.out = new ObjectOutputStream(s.getOutputStream());
			this.in = new ObjectInputStream(s.getInputStream());
			this.cipher=c;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String msg){
		try {
			int padding = 16 - msg.length()%16;
			
			//Prepare to encrypt
			byte[] encoded = Arrays.copyOf(msg.getBytes(), msg.length() + padding );
			
			//Encrypt and generate MAC
			byte[] msgEncrypted = cipher.doFinal(encoded);
			byte[] mac = macHandler.doFinal(msgEncrypted);
			
			//Create Object and Send it
			Message toSend = new Message(msgEncrypted, mac);
			out.writeObject(toSend);
			out.flush();
			
		} catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String receiveMessage(){
		Message received;
		String msg = "Recebeu sem sucesso!!\n";
		try {
			//Receive Object
			received = (Message)in.readObject();
			
			//Mac Autentication
			if(Arrays.equals(received.getMac(), macHandler.doFinal(received.getMsg()))){
				
				//Decrypt and get message
				msg = new String(cipher.doFinal(received.getMsg()));
			}else
				msg = "MAC não é aceite!!";
			
		} catch ( IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	
	public PublicKey SendRecvKey(PublicKey publicKey){
		try {
			out.writeObject(publicKey);
			out.flush();
			publicKey = (PublicKey)in.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return publicKey;
	}
	
	public void setMac(byte[] sessionKey){
		try {			
			SecretKeySpec key = new SecretKeySpec(Arrays.copyOfRange(sessionKey, 0, 15), "HmacSHA1");
			this.macHandler = Mac.getInstance(key.getAlgorithm());
			this.macHandler.init(key);

		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setCipher(Cipher c){
		this.cipher = c;
	}
	
}
