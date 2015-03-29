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
			
			//Prepare to encrypt and generate MAC
			byte[] encoded = Arrays.copyOf(msg.getBytes(), msg.length() + padding );
			byte[] mac = macHandler.doFinal(encoded);
			
			//Encrypt
			byte[] msgEncrypted = cipher.doFinal(encoded);
			byte[] macEncrypted = cipher.doFinal(mac);
			
			Message toSend = new Message(msgEncrypted, macEncrypted);
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
			received = (Message)in.readObject();
			
			byte[] decoded = cipher.doFinal(received.getMsg());
			byte[] mac = cipher.doFinal(received.getMac());
			
			if(Arrays.equals(mac, macHandler.doFinal(decoded)))
				msg = new String(decoded);
			else
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
			SecretKeySpec key = new SecretKeySpec(sessionKey, "HmacMD5");
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
