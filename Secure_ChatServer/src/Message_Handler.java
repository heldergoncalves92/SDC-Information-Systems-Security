import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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


public class Message_Handler extends Thread{
	
	private Certs certs;
	private Cipher cDecrypt, cEncrypt;
	private Mac macHandler;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Boolean listening = false;
	
	public Message_Handler(Socket s){
		try {
			this.out = new ObjectOutputStream(s.getOutputStream());
			this.in = new ObjectInputStream(s.getInputStream());
			this.certs = new Certs();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Message_Handler(Socket s, Cipher cDecrypt, Cipher cEncrypt){
		try {
			this.certs = new Certs();
			this.out = new ObjectOutputStream(s.getOutputStream());
			this.in = new ObjectInputStream(s.getInputStream());
			this.cDecrypt = cDecrypt;
			this.cEncrypt = cEncrypt;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setListening(Boolean b){
		this.listening = b;
	}
	
	public byte[] objToByteArray(Object obj){
		byte[] data = null;
		
		try {
			ByteArrayOutputStream  bos = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(bos);
			oout.writeObject(obj);
			data = bos.toByteArray();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public Object byteArrayToObj(byte[] data){
		Object obj = null;
		try {
			ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(data));
			obj = oin.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	public void sendMessage(Object msg){
		byte[] toEncode;
		try {
			toEncode = objToByteArray(msg);
			
			int padding;
			int auxPad = toEncode.length%16;
			
			if(auxPad == 0)	padding = 0;
			else padding = 16 - auxPad;
			
			//Prepare to encrypt
			byte[] encoded = Arrays.copyOf(toEncode, toEncode.length + padding );
			
			//Encrypt and generate MAC
			byte[] msgEncrypted = cEncrypt.doFinal(encoded);
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
	
	public Object receiveMessage() throws ClassNotFoundException, IOException{
		byte[] data;
		Message received;
		Object msg = "Recebeu sem sucesso!!\n";
		try {
			//Receive Object
			received = (Message)in.readObject();
			
			//Mac Autentication
			if(Arrays.equals(received.getMac(), macHandler.doFinal(received.getMsg()))){
				
				//Decrypt and get message
				
				data = cDecrypt.doFinal(received.getMsg());
				msg = byteArrayToObj(data);
				/*
				if(msg.getClass().equals("java.lang.String")){
					String s = (String)msg;
					System.out.println("É uma String" + s.trim());
				}*/
			}else
				msg = "MAC não é aceite!!";
			
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			//e.printStackTrace();
		}
		return msg;
	}
	
	public PublicKey SendRecvKey(PublicKey publicKey){
		try {
			//To send signedKey
		//	byte[] signed = certs.signMsg(publicKey.getEncoded());
			
			out.writeObject(publicKey);
			out.flush();
			publicKey = (PublicKey)in.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return publicKey;
	}
	
	
	public boolean testSignature(PublicKey key){
		try {
			//Sign Message
			byte[] signed = certs.signMsg(key.getEncoded());
			Message m = new Message(signed);
			out.writeObject(m);
			out.flush();
			
			m = (Message)in.readObject();
			
			if(certs.verifyMsg(m.getMsg()))
				return true;
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
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
	
	public void setCipher(Cipher cDecrypt, Cipher cEncrypt){
		this.cDecrypt = cDecrypt;
		this.cEncrypt = cEncrypt;
	}
	
	public void initToVerify(){
		this.certs.initToVerify();
	}
	
	public void initToSign(){
		this.certs.initToSign();
	}
	
	//To listening new messages
	public void run(){
		String recv = "";
		
		try{
			while(true){
				
				while(listening == false) Thread.sleep(1000);
				
				//System.out.println("Client Start Listning");
				while(true){
					recv = (String)this.receiveMessage();	
					recv.trim();
					
					//Write on screen
					System.out.println(recv);
					
					if(recv.equals("LOGOUT")) break;

				}
				listening = false;
				//System.out.println("Client Close Listening");
			}
		}catch(Exception e){
			System.out.println("ERROR");
		}
		System.out.println("Client Close Connection!!");
	}
	
}
