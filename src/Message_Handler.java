import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import com.sun.xml.internal.messaging.saaj.util.Base64;


public class Message_Handler {
	
	private Cipher cipher;
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
			byte[] encoded = Base64.encode(msg.getBytes());
			byte[] encrypted = cipher.doFinal(encoded);
			
			Message toSend = new Message(encrypted);
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
			byte[] decrypted = cipher.doFinal(received.getMsg());
			msg = Base64.base64Decode(new String(decrypted));
			
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
	
	public void setCipher(Cipher c){
		this.cipher = c;
	}
	

	/*public static void main(String[] args) throws UnsupportedEncodingException {
		String msg = "Tudo bem!";
		System.out.println(msg.length());
		byte[] encoded = Base64.encode(msg.getBytes());
		
		System.out.println(encoded);
		
		String decoded = Base64.base64Decode(new String(encoded));
		System.out.println("Resultado: "+ decoded);
		
		
	}*/
	
}
