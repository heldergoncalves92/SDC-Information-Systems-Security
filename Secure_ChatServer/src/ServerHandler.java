
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;


public class ServerHandler extends InitCipher implements Runnable {

	private int id;
	private String userLogged = "";
	private Socket socket;
	private Message_Handler handler;
	private BufferedWriter file;
	private HashMap<Integer,ServerHandler> online;
	private SQLiteJDBC db = new SQLiteJDBC();
	private Logger logger;
	
	
	private byte[] iv = "1234567812345678".getBytes();
	
	public ServerHandler(int num, Socket s, BufferedWriter file, HashMap<Integer,ServerHandler> online, Logger logger){
		
		this.id=num;
		this.socket=s;
		this.file = file;
		this.online = online;
		this.logger = logger;
		PublicKey publicKey;
		Cipher cDecrypt, cEncrypt;
		
		//To accord the Diffie-Hellman Key
		DiffieHellman df = new DiffieHellman();
		KeyPair kPair = df.generateKey();
			
		//Init Message_Handler and Get PublicKey
		this.handler = new Message_Handler(s);
		publicKey = handler.SendRecvKey(kPair.getPublic());
		
		//Generate SessionKey
		byte[] key = df.sessionKey(kPair.getPrivate(), publicKey);
		
		//Init Cipher
		String type = "AES/CBC/NoPadding";	
		cDecrypt = initCipherByType(type, Cipher.DECRYPT_MODE, key, iv);
		cEncrypt = initCipherByType(type, Cipher.ENCRYPT_MODE, key, iv);
		
		//Prepare Message_Handler with Cipher
		handler.setCipher(cDecrypt, cEncrypt);
		handler.setMac(key);
		
		handler.initToSign();
		handler.initToVerify();
		handler.testSignature(publicKey);
	}
	
	public String getUserLogged(){
		return this.userLogged;
	}
	
	
	private void register() throws ClassNotFoundException, IOException {
		String username, password, email;
		Register reg;
		
		while(true){
			reg = (Register)handler.receiveMessage();
	
			password = reg.getPassword();
			username = reg.getUsername();
			email = reg.getEmail();
			
			if(!this.db.containsUser(username)){
				
				db.insertUser(username, password, email);
				handler.sendMessage("OK");
				logger.log(Level.INFO, "NEW REGISTER!! User: " + username);
				break;
			
			} else{
				handler.sendMessage("USER_EXIST");
			}
		}
	}
	
	private boolean loginUser() throws ClassNotFoundException, IOException{
		String username, password;
		Login login;
		int nTry;
		
		while(true){
			login = (Login)handler.receiveMessage();
	
			password = login.getPassword();
			username = login.getUsername();
			nTry = db.getTryByUser(username);
			
			if(nTry>3){
				handler.sendMessage("BLOCKED");
				logger.log(Level.SEVERE, "User " + username + "was BLOCKED!! WRONG password!");
				return false;
			}
			
			if(this.db.checkLogin(username, password)){
				if(db.isUserLogged(username)){
					
					db.setTryByUser(username, 4);
					handler.sendMessage("BLOCKED");
					logger.log(Level.SEVERE, "User " + username + "was BLOCKED!! Someone is trying HACK the account!");
					
					searchAndWarning(username);
					return false;
				}
					
				handler.sendMessage("OK");
				db.setTryByUser(username, 0);
				logger.log(Level.FINE, "LOGIN successful! User: " + username);
				
				this.userLogged = username;
				db.setUserLogged(username, 1);
				return true;
			
			} else{
				handler.sendMessage("NOK");
				db.setTryByUser(username, nTry+1);
				
			}
		}
	}
	
	private boolean secureMode() throws ClassNotFoundException, IOException, InterruptedException {
		String username, email = "", code = "", password;
		Email mailServer = new Email();
		int nTry = 0;
		
		username = (String)handler.receiveMessage();
		username.trim();
		
		email = db.getMailByUser(username);
		
		if(email == null){
			this.handler.sendMessage("INVALID_USER");
			return false;
		}
		System.out.println(email);
		
		code = mailServer.sendEmail(email, username);
        System.out.println(code);
        
        
        this.handler.sendMessage("WAITING");
        long time = new GregorianCalendar().getTimeInMillis();
        logger.log(Level.CONFIG, "Generated SECURE_CODE! User: " + username);
        
        while(true){
        	nTry++;
        
	        String uCode = (String)handler.receiveMessage();
	        uCode.trim();
	        
	        //Code is valid for 5 minutes
	        if((new GregorianCalendar().getTimeInMillis()-time)/1000 > 300){
	        	handler.sendMessage("TIME_EXCEDED");
	        	logger.log(Level.WARNING, "Time to SECURE_CODE exceeded! User: " + username);
	        	return false;
	        
	        } else if(uCode.equals(code)){
	        	db.setTryByUser(username, 0);
	        	handler.sendMessage("OK");
	        	logger.log(Level.INFO, "SECURE_CODE successful! User: " + username);
	        	
	        	uCode = (String)handler.receiveMessage();
		        uCode.trim();
		        
		        if(uCode.equals("YES_CHANGE")){
		        	password = (String)handler.receiveMessage();
			        password.trim();
			        db.changePassword(username, password);
			        logger.log(Level.SEVERE, "PASSWORD changed! User: " + username);
		        }
	        	return true;
	        
	        } else{
	        	if(nTry==3){
	        		handler.sendMessage("TRYS_EXCEDED");
	        		logger.log(Level.WARNING, "Number of SECURE_CODE trys was exceeded! User: " + username);
	        		return false;
	        	}
	        	
	        	handler.sendMessage("NOK");
	        	logger.log(Level.INFO, "SECURE_CODE was wrong! User: " + username);
	        }
        } 
	}
	
	
	public void run() {
		String recv;
		//boolean security = true;
		
		try{
			while(true){
				
				while(true){
					recv = (String)handler.receiveMessage();
					recv.trim();
				
					if(recv.equals("LOGIN")){
						logger.log(Level.FINEST, "New Login!");
						if(loginUser()) break; 
						
					
					}else if(recv.equals("SECURE_MODE")){
						logger.log(Level.FINE, "SECURE_CODE Request");
						secureMode();
					
					}else if(recv.equals("REGISTER")){
						logger.log(Level.FINER, "Register Request");
						register();
					}
				}
						
				System.out.println("Entrou no sistema!!");
				this.online.put(id, this);
								
				while(true){
					recv = (String)handler.receiveMessage();
					recv.trim();
					
					//Send the message to everyone online
					this.sendToAllOnline(recv);
					
					//Reserved words
					if(recv.endsWith(": OUT")){
						db.setUserLogged(this.userLogged, 0);
						this.handler.sendMessage("LOGOUT");
						logger.log(Level.FINE, "User Logout");
						break;
					}
					
					//Write on screen
					System.out.println(recv);
					
	            }
				this.online.remove(id);	
			}
			
		}catch(IOException | ClassNotFoundException | InterruptedException e){}
		
		finally{
			this.online.remove(id);
			db.setUserLogged(this.userLogged, 0);
			logger.log(Level.CONFIG, "Connection Close");
			
			//Write on screen
			System.out.println("=["+ id +"]=\n");
			
			//Write on file
			try {
				file.write("=["+ id +"]=\n");
				file.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	private void searchAndWarning(String user){
		ServerHandler s = null;
		for(ServerHandler aux: this.online.values()){
			if(aux.getUserLogged().equals(user)){
				s = aux;
				break;
			}
		}
		if(s != null)
			s.sendMessage(
						"*****************************************\n"
					+	"*****************************************\n"
					+ 	"***********      DANGER      ************\n"
					+ 	"*****************************************\n"
					+ 	"*****************************************\n"
					+	"******     Someone is trying to    ******\n"
					+ 	"******      use your account!!     ******\n"
					+	"*****************************************\n"
					+	"*****************************************\n"
					+	"***** The SECURE MODE was activated! ****\n"
					+	"*****************************************\n");
		
	}
	
	public void sendMessage(String msg){
		this.handler.sendMessage(msg);
	}
	
	private void sendToAllOnline(String msg){
		for(ServerHandler s: online.values()){
			if(!s.equals(this))
				s.sendMessage(msg);
		}
	}
	
	public void closeConn(){
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
